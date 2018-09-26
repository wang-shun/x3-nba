package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.CoachConsole;
import com.ftkj.constant.ChatPushConst;
import com.ftkj.db.ao.logic.ICoachAO;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.coach.Coach;
import com.ftkj.manager.coach.CoachBean;
import com.ftkj.manager.coach.TeamCoach;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.CoachPB;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang 2017年9月13日 教练管理
 */
public class CoachManager extends BaseManager implements OfflineOperation {

	private Map<Long, TeamCoach> coachMap;

	@IOC
	private ICoachAO coachAO;

	@IOC
	private TeamManager teamManager;

	@IOC
	private TaskManager taskManager;
	
	@IOC
	private ChatManager chatManager;

	public TeamCoach getTeamCoach(long teamId) {
		TeamCoach coach = this.coachMap.get(teamId);
		if (coach == null) {
			List<Coach> coachList = coachAO.getTeamCoach(teamId);
			// if(coachList.size()<=0){
			// CoachConsole.getCoachList().forEach(cc->coachList.add(new
			// Coach(teamId,cc.getcId(),cc.getTeamId())));
			// coachList.forEach(cc->cc.save());
			// }
			coach = new TeamCoach(coachList);
			this.coachMap.put(teamId, coach);
		}
		return coach;
	}

	public void addCoach(long teamId, int coachId) {
		TeamCoach teamCoach = getTeamCoach(teamId);
		CoachBean bean = CoachConsole.getCoachBean(coachId);
		if (bean == null) {
			return;
		}
		Coach coach = teamCoach.getTeamCoach(coachId);
		if (coach != null) {
			return;
		}
		teamCoach.addCoach(new Coach(teamId, coachId, bean.getcId()));
		RedPointParam rpp = new RedPointParam(teamId, ERedPoint.教练获得.getId(), 1);
		EventBusManager.post(EEventType.奖励提示, rpp);
		taskManager.updateTask(teamId, ETaskCondition.解锁教练, 1, "" + coachId);
		
		// 解锁教练广播
		Team team = teamManager.getTeam(teamId);
		if(bean.getCoachLevel() >= ChatPushConst.CHAT_PUSH_COACH_LEVEL_3) {
			chatManager.pushGameTip(EGameTip.获得B_S级教练, 0, team.getName(), bean.getCoachName());
		}
	}

	@ClientMethod(code = ServiceCode.CoachManager_showCoachMain)
	public void showCoachMain() {
		long teamId = getTeamId();
		TeamCoach teamCoach = getTeamCoach(teamId);
		sendMessage(getCoachMainData(teamCoach));
	}

	@ClientMethod(code = ServiceCode.CoachManager_signCoach)
	public void signCoach(int cid) {
		long teamId = getTeamId();
		TeamCoach teamCoach = getTeamCoach(teamId);
		Coach coach = teamCoach.getCoachList().stream().filter(c -> c.getCid() == cid).findFirst().orElse(null);
		if (coach == null) {
			log.debug("传入不存在的教练ID[{}]", cid);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		Coach defaultCoach = teamCoach.getDefaultCoach();
		if (defaultCoach != null) {
			defaultCoach.updateStatus(EStatus.Close.getId());
		}
		coach.updateStatus(EStatus.Open.getId());
		taskManager.updateTask(teamId, ETaskCondition.任命教练, 1, EModuleCode.教练.getName());
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}

	private CoachPB.CoachMainData getCoachMainData(TeamCoach teamCoach) {
		List<CoachPB.CoachData> datas = teamCoach.getCoachList().stream().map(coach -> getCoachData(coach))
				.collect(Collectors.toList());
		return CoachPB.CoachMainData.newBuilder().addAllCoachList(datas).build();
	}

	private CoachPB.CoachData getCoachData(Coach coach) {

		return CoachPB.CoachData.newBuilder().setCid(coach.getCid()).setStatus(coach.getStatus()).build();

	}

	@Override
	public void instanceAfter() {
		this.coachMap = Maps.newConcurrentMap();
	}

	@Override
	public void offline(long teamId) {
		coachMap.remove(teamId);
	}

	@Override
	public void dataGC(long teamId) {
		coachMap.remove(teamId);
	}
}
