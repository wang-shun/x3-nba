package com.ftkj.action;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.BaseRobot;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class DefaultAction extends BaseAction<ZGameRobot> {

	// 协议类
	@SuppressWarnings("rawtypes")
	private static Map<Integer,Class> clazzMap = new HashMap<Integer, Class>();;
	
	/**
	 * 简单调用接口的方法
	 * @param serviceCode
	 * @return
	 */
	public static BaseAction<BaseRobot> instanceService(int serviceCode) {
		return instanceService(serviceCode, DefaultPB.DefaultData.class);
	}
	
	@SuppressWarnings("rawtypes")
	public static BaseAction<BaseRobot> instanceService(int serviceCode, Class cla) {
		DefaultAction ac = new DefaultAction(serviceCode);
		ac.addClazz(serviceCode, cla);
		return ac.get();
	}
	
	/**
	 * 通过接口好来初始化行为，默认主题行为
	 * @param servicecCode
	 */
	public DefaultAction(int servicecCode) {
		super(BaseAction.Main_Action, servicecCode);
	}

	public void run(ZGameRobot robot) {
		robot.actionJob(get());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void callback(ClientResponse response, ZGameRobot robot) throws Exception {
		int serviceCode = response.getKey();
		// 排除其他推包
		if(!clazzMap.containsKey(serviceCode)) {
			System.err.println("不处理回包["+serviceCode+"]");
			return;
		}
		System.err.println("回调成功["+ serviceCode +"]:");
		Class cla = clazzMap.get(serviceCode);
		Method pbParse = cla.getMethod("parseFrom", byte[].class);
		
		//if(b.length>1020){
//		this.iszip = true;
//		LoggerManager.debug("压缩前[{}]", b.length);
//		this.bytes = ByteUtil.compress(b);
//		LoggerManager.debug("压缩后[{}]", this.bytes.length);
		
		byte[] datas = response.getData();
		Object data = pbParse.invoke(cla, datas);
		// 保存teamId
		if(serviceCode == ServiceCode.GameManager_debugLogin) {
			DefaultPB.DefaultData dataObject = (DefaultData) data;
			robot.setTeamId(dataObject.getBigNum());
		}
		String res = data.toString();
		System.err.println(res);
		Thread.sleep(500);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void run(ZGameRobot robot, Object... val) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("rawtypes")
	private void addClazz(int serviceCode, Class clazz) {
		this.clazzMap.put(serviceCode, clazz);
	}

}
