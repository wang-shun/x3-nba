package com.ftkj.manager.active.recharge;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.active.recharge.AtvFirstRechargeDbManager.AtvFirstRechargeDB;
import com.ftkj.proto.FirstRechargePB.FirstRechargeData;
import com.ftkj.server.ServiceCode;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventRegister({EEventType.充值})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.首冲双倍, clazz=AtvFirstRechargeDB.class)
public class AtvFirstRechargeDbManager extends ActiveBaseManager {
    private static final Logger log = LoggerFactory.getLogger(AtvFirstRechargeDbManager.class);
	/**
	 * 充值回调
	 * @param param
	 */
	@Subscribe
	public void addMoneyFK(RechargeParam param) {
		if(getStatus(param.time) != EActiveStatus.进行中) return;
		long teamId = param.teamId;
		AtvFirstRechargeDB atvObj = getTeamData(teamId);
		boolean isItem = getAwardConfigList().values().stream().anyMatch(s-> Integer.valueOf(s.getConditionMap().get("fk")) == param.fk);
		log.debug("首冲双倍赠送活动[teamId={}, fk={}, isItem={}, 已获得双倍档={}]", param.teamId, param.fk, isItem, atvObj.getItems().getValueStr());
		if(isItem && !atvObj.getItems().containsValue(param.fk)) {
			atvObj.getItems().addValue(param.fk);
			atvObj.save();
			// 再发一倍球券
			moneyManager.updateTeamMoney(teamId, 0, 0, 0, param.fk, true, getActiveModuleLog());
			atvObj.save();
		}
	}

	/**
	 * 首冲状态
	 */
	@Override
	@ClientMethod(code = ServiceCode.AtvFirstRechargeDBManager_rechargeView)
	public void showView() {
		long teamId = getTeamId();
		AtvFirstRechargeDB atvObj = getTeamData(teamId);
		sendMessage(FirstRechargeData.newBuilder().addAllStatus(atvObj.getItems().getList()).build());
	}
	
	/**
	 * 活动数据类
	 * @author Jay
	 * @time:2017年9月18日 下午12:00:02
	 */
	public static class AtvFirstRechargeDB extends ActiveBase {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AtvFirstRechargeDB(ActiveBasePO po) {
			super(po);
		}
		
		@ActiveDataField(fieldName = "sData1")
		private DBList items;

		public int getMoney() {
			return this.getiData1();
		}

		public void setMoney(int money) {
			this.setiData1(money);
		}

		public DBList getItems() {
			return items;
		}
	}
	
}
