package com.ftkj.manager.active.recharge;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.server.ServiceCode;
import com.google.common.eventbus.Subscribe;


/**
 * 每日充值特惠
 * @author Jay
 * @time:2017年9月18日 上午11:21:32
 */
@EventRegister({EEventType.购买每日充值特惠})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.每日充值特惠, clazz=AtvEveryDayRechargeManager.AtvEveryDayRecharge.class)
public class AtvEveryDayRechargeManager extends ActiveBaseManager {

	/**
	 * 充值回调
	 * @param param
	 */
	@Subscribe
	public void buyItem(RechargeParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		long teamId = param.teamId;
		AtvEveryDayRecharge atvObj = getTeamDataRedisDay(teamId);
		SystemActiveCfgBean cfg = getAwardConfigList().values().stream().filter(s-> s.getConditionMap().get("fk").equals(param.fk+"")).findFirst().orElse(null);
		if(cfg == null) {
			return;
		}
		if(atvObj.buyItem.getValue(cfg.getId())+1 > Integer.valueOf(cfg.getConditionMap().get("limit"))) {
			return;
		}
		moneyManager.updateTeamMoneyUnCheck(moneyManager.getTeamMoney(teamId), param.fk, 0, 0, 0, true, getActiveModuleLog());
		atvObj.buyItem.setValueAdd(cfg.getId(), 1);
		propManager.addPropList(teamId, cfg.getPropSimpleList(), true, getActiveModuleLog());
		saveDataReidsDay(teamId, atvObj);
	}
	
	/**
	 * 主界面
	 * 今天的购买状态
	 */
	@Override
	@ClientMethod(code = ServiceCode.AtvEveryDayRechargeManager_showView)
	public void showView() {
		long teamId = getTeamId();
		AtvEveryDayRecharge atvObj = getTeamDataRedisDay(teamId);
		sendMessage(AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				.addAllFinishStatus(atvObj.buyItem.getList())
				.setValue(0)
				.build());
	}
	
	
	/**
	 * 活动数据类
	 * @author Jay
	 * @time:2017年9月18日 下午12:00:02
	 */
	public static class AtvEveryDayRecharge extends ActiveBase {

		private static final long serialVersionUID = 1L;

		public AtvEveryDayRecharge(ActiveBasePO po) {
			super(po);
		}
		@ActiveDataField(fieldName = "sData1", size = 7)
		private DBList buyItem;
	}
}
