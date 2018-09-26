package com.ftkj.manager.active.recharge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LevelupParam;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.ServiceCode;
import com.google.common.eventbus.Subscribe;

/**
 * 成长基金福利
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.购买成长基金, EEventType.球队升级})
@ActiveAnno(redType=ERedType.福利, atv = EAtv.成长基金福利, clazz=FLGroupUpFundManager.AtvGroupUpFund.class)
public class FLGroupUpFundManager extends ActiveBaseManager {
	private static final Logger log = LoggerFactory.getLogger(FLGroupUpFundManager.class);
	public static class AtvGroupUpFund extends ActiveBase {
		private static final long serialVersionUID = 1L;

		public AtvGroupUpFund(ActiveBasePO po) {
			super(po);
		}

		@ActiveDataField(fieldName = "sData1")
		private DBList status;

		public DBList getStatus() {
			return status;
		}
		public void setAddMoney(int money) {
			this.setiData1(money);
		}
		public int getAddMoney() {
			return this.getiData1();
		}
	}
	
	/**
	 * 充值回调
	 * @param param
	 */
	@Subscribe
	public void addMoneyFK(RechargeParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		long teamId = param.teamId;
		// 这里添加球券，应该在回调前统一处理
		moneyManager.updateTeamMoney(teamId, param.fk, 0, 0, 0, true, getActiveModuleLog());
		if(param.fk != getConfigInt("money", 0)) {
			return;
		}
		// 只能购买一次
		AtvGroupUpFund atvObj = getTeamData(teamId);
		if(atvObj.getAddMoney() > 0) {
			log.debug("只能购买一次成长基金!");
			return;
		}
		atvObj.setAddMoney(param.fk);
		atvObj.save();
		// 可领提示更新
		redPointPush(teamId);
	}
	
	/**
	 * 球队升级
	 * @param param
	 */
	@Subscribe
	public void teamLvUp(LevelupParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		AtvGroupUpFund atvObj = getTeamData(param.teamId);
		if(atvObj.getAddMoney() < 1) {
			return;
		}
		// 可领提示更新
		redPointPush(param.teamId);
	}
	
	@Override
	@ClientMethod(code = ServiceCode.FLGroupUpFundManager_showView)
	public void showView() {
		long teamId = getTeamId();
		AtvGroupUpFund atvObj = getTeamData(teamId);
		sendMessage(AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				// 是0可以购买，1是已经购买过
				.setValue(atvObj.getAddMoney() > 0 ? 1 : 0)
				.addAllAwardStatus(atvObj.getStatus().getList())
				.build());
	}
	
	@ClientMethod(code = ServiceCode.FLGroupUpFundManager_getAward)
	public void getAward(int level) {
		long teamId = getTeamId();
		int teamLevel = teamManager.getTeam(teamId).getLevel();
		SystemActiveCfgBean awardCfg = getBean().getAwardConfigList().get(level);
		if(awardCfg == null) {
			log.debug("该等级{}没有可领取的奖励!", level);
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_3.code).build());
			return;
		}
		if(teamLevel < level) {
			log.debug("条件不满足!", level);
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_5.code).build());
			return;
		}
		// 是否已领
		AtvGroupUpFund atvObj = getTeamData(teamId);
		if(atvObj.getStatus().containsValue(level)) {
			log.debug("已领取过该奖励!");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_7.code).build());
			return;
		}
		if(atvObj.getAddMoney() == 0) {
			log.debug("未参加该活动");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Active_5.code).build());
			return;
		}
		// 领取球券
		atvObj.getStatus().addValue(level);
		atvObj.save();
		propManager.addPropList(teamId, getAwardConfigList().get(level).getPropSimpleList(), true, getActiveModuleLog());
		sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		// 可领提示更新
		redPointPush(teamId);
	}

	@Override
	public boolean checkHideWindow(long teamId) {
		AtvGroupUpFund atvObj = getTeamData(teamId);
		return atvObj.getStatus().getSize() == getBean().getAwardConfigList().size();
	}
	
	
	/**
	 * 奖励变更提示
	 */
	@Override
	public int redPointNum(long teamId) {
		AtvGroupUpFund atvObj = getTeamData(teamId);
		int num = 0;
		if(atvObj.getAddMoney() > 0) {
			int teamLevel = teamManager.getTeam(teamId).getLevel();
			num = (int) getBean().getAwardConfigList().values().stream()
					.mapToInt(s-> Integer.valueOf(s.getConditionMap().get("level"))).filter(lv-> lv <= teamLevel).count() - atvObj.getStatus().getSize();
		}
		return num;
	}
}
