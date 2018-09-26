package com.ftkj.manager.active.logins;

import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;
import com.google.common.eventbus.Subscribe;

/**
 * 八天累计登录
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.登录})
@ActiveAnno(redType = ERedType.福利, atv = EAtv.八天登录福利, clazz=AtvDaysLogin.class)
public class FLDaysLoginManager extends ActiveBaseManager {
    private static final Logger log = LoggerFactory.getLogger(FLDaysLoginManager.class);
	private static final int DAY_COUNT = 8;
	
	/**
	 * 登录回调
	 * @param param
	 */
	@Subscribe
	public void login(LoginParam param) {
		if(getStatus(param.loginTime) != EActiveStatus.进行中) return;
		long teamId = param.teamId;
		if(!param.isTodayFirst) return;
		AtvDaysLogin atvObj = getTeamData(teamId);
		DateTime now = DateTime.now();
		synchronized (atvObj) {
			boolean isToday = DateTimeUtil.getDaysBetweenNum(atvObj.getLastTime(), now, 0) == 0;
			if(atvObj.getLoginDay() >= DAY_COUNT || isToday) {
				return;
			}
			//累计天数加1
			atvObj.addLoginDay(1);
			atvObj.setLastTime(param.loginTime);
			atvObj.save();
		}
		
		log.error("=========FLDaysLoginManager===teamId|{}==登录天数是:{}=======", teamId, atvObj.getLoginDay());
		// 可领提示更新
		redPointPush(teamId);
	}

	/**
	 * 主界面
	 */
	@ClientMethod(code = ServiceCode.FLDaysLoginManager_showView)
	public void showView() {
		long teamId = getTeamId();
		AtvDaysLogin atvObj = getTeamData(teamId);
		sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				.setValue(atvObj.getLoginDay())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
	}
	
	/**
	 * 领取奖励
	 * @param day 第几天 1开始
	 */
	@ClientMethod(code = ServiceCode.FLDaysLoginManager_getAward)
	public void getAward(int day) {
		long teamId = getTeamId();
		Map<Integer,SystemActiveCfgBean> awardCfg = getBean().getAwardConfigList();
		if(awardCfg == null || !awardCfg.containsKey(day)) {
			log.debug("兑换奖励类型[{}]没有找到!", day);
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 是否已领
		AtvDaysLogin atvObj = getTeamData(teamId);
		if(atvObj.getAwardStatus().containsValue(day)) {
			log.debug("已领取过该奖励!");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(atvObj.getLoginDay() < day) {
			log.debug("未满足领取条件");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 领奖励
		atvObj.getAwardStatus().addValue(day);
		atvObj.save();
		propManager.addPropList(teamId, getAwardConfigList().get(day).getPropSimpleList(), true, getActiveModuleLog());
		sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		// 可领提示更新
		redPointPush(teamId);
	}

	@Override
	public boolean checkHideWindow(long teamId) {
		AtvDaysLogin atvObj = getTeamData(teamId);
		return atvObj.getLoginDay() >= DAY_COUNT && atvObj.getAwardStatus().getSize() == DAY_COUNT;
	}
	
	/**
	 * 奖励红点计算
	 */
	@Override
	public int redPointNum(long teamId) {
		// 发送事件
		AtvDaysLogin atvObj = getTeamData(teamId);
		int num = atvObj.getLoginDay() != atvObj.getAwardStatus().getSize() ? 1 : 0;
		return num;
	}
}
