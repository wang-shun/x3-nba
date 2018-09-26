package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.db.ao.logic.ICustomerServiceAO;
import com.ftkj.db.domain.CustomerServicePO;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.customerservice.CustomerService;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.CustomerServicePB;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mr.lei
 * 2018年8月31日14:31:50
 * 客服工单管理
 */
public class CustomerServiceManager extends BaseManager implements OfflineOperation {
	
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceManager.class);
    
    @IOC
    private ICustomerServiceAO customerServiceAO;
    @IOC
    private TeamManager teamManager;
    @IOC
    private VipManager vipManager;
    private AtomicLong ids = new AtomicLong(0);
    /** key = teamId球队Id, value=客服工单数据*/
    private Map<Long, List<CustomerService>> customerServiceMap;
    
	@Override
	public void instanceAfter() {
		this.customerServiceMap = Maps.newConcurrentMap();
		//考虑到一个性能问题,启动游戏服务器就把玩家的所有数据查询出来,30天后的数据和标记已经删除的数据不查询出来
		List<CustomerServicePO> tmpList = this.customerServiceAO.getAllCustomerService();
		initData(tmpList);
		this.ids.set(customerServiceAO.getRowCount());
	}
	
	private void initData(List<CustomerServicePO> list){
		for (CustomerServicePO po : list) {
			addCustomerServiceData(new CustomerService(po));
		}
	}
	
	private void addCustomerServiceData(CustomerService obj){
		long teamId = obj.getPo().getTeamId();
		List<CustomerService> list = this.customerServiceMap.get(teamId);
		if (list == null) {
			list = Lists.newArrayList();
			this.customerServiceMap.put(teamId, list);
		}
		
		list.add(obj);
	}
	
	@Override
	public void offline(long teamId) {
		this.customerServiceMap.remove(teamId);
	}
	
	@Override
	public void dataGC(long teamId) {
		this.customerServiceMap.remove(teamId);
	}
	
	/**
	 * 获取主键Id
	 * @return
	 */
	private long getCustomerServiceNewId() {
        return GameSource.shardId * 10000 + ids.incrementAndGet();
    }
    
	/**
	 * 获取所有客服工单数据.
	 */
    @ClientMethod(code = ServiceCode.CustomerServiceManager_getPlayerCustomerServiceData)
    public void getPlayerCustomerServiceData(){
    	long teamId = getTeamId();
    	CustomerServicePB.CustomerServiceMsgList.Builder builder = CustomerServicePB.CustomerServiceMsgList.newBuilder();
		List<CustomerService> list = this.customerServiceMap.get(teamId);
		if (list == null) {
			List<CustomerServicePO> tmpList = this.customerServiceAO.getPlayerCustomerServiceData(teamId);
			for (CustomerServicePO po : tmpList) {
				builder.addCsMsgList(buildCustomerServiceMsgData(po));
				addCustomerServiceData(new CustomerService(po));
			}
			
			//如果玩家没有提问,则放入一个空集合
			if (tmpList.size() == 0) {
				this.customerServiceMap.put(teamId, new ArrayList<CustomerService>(1));
			}
		}else {
			for (CustomerService obj : list) {
				builder.addCsMsgList(buildCustomerServiceMsgData(obj.getPo()));
			}
		}
    	
		sendMessage(getTeamId(), builder.build(), ServiceCode.CustomerServiceManager_getPlayerCustomerServiceData);
    }
    
    /**
     * 玩家提交问题.
     * @param telephone		手机号码
     * @param qq			QQ号码
     * @param problem		问题描述
     * @param occurTime		发生问题的时间
     */
    @ClientMethod(code = ServiceCode.CustomerServiceManager_playerCommitGameProblem)
    public void playerCommitGameProblem(String telephone, String qq, String problem, String occurTime){
    	long teamId = getTeamId();
    	long csId = getCustomerServiceNewId();
		String areaName = GameSource.shardId + "";
		Team team = teamManager.getTeam(teamId);
		int vipLevel = vipManager.getVip(teamId).getLevel();
		String playerName = team.getName();
		CustomerService customerService = CustomerService.createCustomerService(csId , areaName, teamId, vipLevel, 
				playerName, telephone, qq, problem, null,"0", occurTime);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		addCustomerServiceData(customerService);
    }
    
    /**
     * 客服通过运营后台回复玩家提的问题,通知客户端有最新回复.
     * @param csId
     * @param respStr
     */
    @RPCMethod(code = CrossCode.WebManager_customerResp, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void gmCustomerServiceResp(long csId, String respStr) {
    	if (csId == 0 || respStr == null || respStr == "") {
			return;
		}
    	
    	CustomerService customerService = null;
    	for (List<CustomerService> tmpList: this.customerServiceMap.values()) {
    		customerService = tmpList.stream().filter(obj -> obj.getPo().getCsId() == csId)
					.findFirst().orElse(null);
    		if (customerService != null) {
				break;
			}
		}
    	
    	if (customerService == null) {
    		CustomerServicePO po = customerServiceAO.getCustomerServiceDataByCsId(csId);
    		if (po == null) {
    			return;
    		}else {
    			customerService = new CustomerService(po);
			}
		}
    	
    	customerService.getPo().setResponse(respStr);
    	customerService.getPo().setRespStatus("1");//状态设置成回复未读
    	customerService.save();
    	long teamId = customerService.getPo().getTeamId();
    	
    	if (GameSource.isOline(teamId)) {
			CustomerServicePB.CustomerServiceMsgNotice.Builder builder = 
				CustomerServicePB.CustomerServiceMsgNotice.newBuilder();
			builder.setCsId(csId);
			builder.setResponse(respStr);
			sendMessage(teamId, builder.build(), ServiceCode.CustomerServiceManager_respNotice);
		}
    }
    
    /**
     * 玩家已经阅读客服的回复.
     * @param csId		提问数据的唯一ID
     */
    @ClientMethod(code = ServiceCode.CustomerServiceManager_playerReadCustomerServiceResp)
    public void playerReadCustomerServiceResp(long csId){
    	long teamId = getTeamId();
    	List<CustomerService> list = this.customerServiceMap.get(teamId);
    	if (list != null) {
			CustomerService customerService = list.stream().filter(obj -> obj.getPo().getCsId() == csId)
					.findFirst().orElse(null);
			// 客服已回复,才标记为回复已读
			if (customerService != null && "1".equals(customerService.getPo().getRespStatus())) {
				customerService.getPo().setRespStatus("2");//状态设置成回复已读
				customerService.save();
			}
		}
    	
    	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }
    
    /**
     * 玩家删除自己所提的问题.
     * @param csId		提问数据的唯一ID
     */
    @ClientMethod(code = ServiceCode.CustomerServiceManager_playerDeleteCustomerServiceData)
    public void playerDeleteCustomerServiceData(long csId){
    	ErrorCode errorCode = playerDeleteCustomerServiceData0(csId);
    	if (errorCode != null) {
    		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(errorCode.code).build());
		}
    }
    
    private ErrorCode playerDeleteCustomerServiceData0(long csId){
    	long teamId = getTeamId();
    	List<CustomerService> list = this.customerServiceMap.get(teamId);
    	
    	if (list == null) {
			return ErrorCode.Fail;
		}
    	
    	CustomerService customerService = list.stream().filter(obj -> obj.getPo().getCsId() == csId)
				.findFirst().orElse(null);
    	if (customerService == null) {
    		return ErrorCode.Fail;
		}
    	
    	// 客服未回复不能删除
    	if ("0".equals(customerService.getPo().getRespStatus())) {
			return ErrorCode.Fail;
		}
    	
    	list.remove(customerService);
		customerService.del();
		if(log.isDebugEnabled()){
			log.debug("playerDeleteCustomerServiceData|teamId = {} | csId = {}", teamId, csId);
		}
		
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		
		return null;
    }

    private CustomerServicePB.CustomerServiceMsgData buildCustomerServiceMsgData(CustomerServicePO po){
    	CustomerServicePB.CustomerServiceMsgData.Builder builder = CustomerServicePB.CustomerServiceMsgData.newBuilder();
    	builder.setAreaName(po.getAreaName());
    	builder.setCreateTime(po.getCreateTime().getMillis());
    	builder.setCsId(po.getCsId());
    	builder.setOccurTime(po.getOccurTime());
    	builder.setPlayerName(po.getPlayerName());
    	builder.setProblem(po.getProblem());
    	builder.setQq(po.getQq() == null ? "" : po.getQq());
    	builder.setResponse(po.getResponse() == null ? "" : po.getResponse());
    	builder.setRespStatus(String.valueOf(po.getRespStatus()));
    	builder.setTeamId(po.getTeamId());
    	builder.setTelphone(po.getTelphone() == null ? "" : po.getTelphone());
    	builder.setVipLevel(po.getVipLevel());
    	
    	return builder.build();
    }
    
}
