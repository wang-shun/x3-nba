package com.ftkj.manager.active;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.console.AnswerQuestionConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.ModuleConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.db.domain.bean.AnswerQuestionBeanVO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.answer.AnswerQuestionData;
import com.ftkj.manager.logic.AbstractBaseManager;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.logic.TeamManager;
import com.ftkj.manager.logic.TeamMoneyManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.AtvAnswerQuestionPB;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PropPB;
import com.ftkj.proto.AtvAnswerQuestionPB.AtvQuestionData;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.redis.JedisUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 答题活动,玩家20级开启.每天可以答题两次,开启的时间是:每天11:00-11:30、20:00-20:30,
 * 每次是10道题目,答对或者答错都有奖励.
 * 答题的状态:0未开始,1开始答题,2答题结束.
 * @author mr.lei
 *
 */
public class AtvAnswerQuestionManager extends AbstractBaseManager implements OfflineOperation{
	private static final Logger log = LoggerFactory.getLogger(AtvAnswerQuestionManager.class);
	@IOC
    private TeamMoneyManager teamMoneyManager;
	@IOC
    private PropManager propManager;
	@IOC
    private TeamManager teamManager;
	@IOC
	private JedisUtil redis;
	
	/**总随机题目的数量*/
	private static final int QUESTION_NUM = 20;
	/**第一次开启时间:11:00-11:30*/
	private static final int[] FIRST_OPEN_TIME = {11 * 60, 11 * 60 + 30};
	/**第二次开启时间:20:00-20:30*/
	private static final int[] SECOND_OPEN_TIME = {20 * 60, 20 * 60 + 30};
	/**第一次答题活动类型*/
	private static final int ATV_FIRST_TYPE = 1;
	/**第二次答题活动类型*/
	private static final int ATV_SECOND_TYPE = 2;
	/**0未答题开始*/
	private static final short ATV_NOT_START = 0;
	/**1答题开始*/
	private static final short ATV_START = 1;
	/**2答题结束*/
	private static final short ATV_END = 2;
	/**答题正确获得的掉落奖励*/
	private static List<DropBean> answerWinDropList;
	/**答题失败获得的掉落奖励*/
	private static List<DropBean> answerLossDropList;
	
	private static final ModuleLog module = ModuleLog.getModuleLog(EModuleCode.AtcAnswerQuestion, "答题活动");
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		initConfigData();
	}
	
	/**
	 * 初始化答题活动中答对和答错的掉落奖励.
	 */
	private void initConfigData(){
		answerWinDropList = Lists.newArrayList();
		answerLossDropList = Lists.newArrayList();
		GlobalBean global = ConfigConsole.getGlobal();
		global.answerWinDropList.forEach(obj -> {
			answerWinDropList.add(DropConsole.getDrop(obj));
		});
		
		global.answerLossDropList.forEach(obj -> {
			answerLossDropList.add(DropConsole.getDrop(obj));
		});
	}
	
	/**
	 * 生成一个玩家答题数据对象.
	 * 包含了当天答题活动的所有随机出的20到题目.
	 * @return
	 */
	private AnswerQuestionData initAnswerQuestionData(){
		List<Integer> questionList = getRandomQuestion(QUESTION_NUM);
		List<Integer> questList =Lists.newArrayList();
		Map<Integer, List<Integer>> map = Maps.newHashMap();
		
		if (questionList == null || questionList.size() == 0) {
			return null;
		}
		
		// 1
		for (int i = 0; i < questionList.size() / 2; i++) {
			questList.add(questionList.get(i));
		}
		
		map.put(ATV_FIRST_TYPE, questList);
		questList =Lists.newArrayList();
		// 2
		for (int i = questionList.size() / 2; i < questionList.size(); i++) {
			questList.add(questionList.get(i));
		}
		
		map.put(ATV_SECOND_TYPE, questList);
		
		Map<Integer, Map<Integer, Integer>> answerMap = Maps.newHashMap();
		answerMap.put(ATV_FIRST_TYPE, Maps.newHashMap());
		answerMap.put(ATV_SECOND_TYPE, Maps.newHashMap());
		
		return new AnswerQuestionData(map, answerMap);
	}
	
	@ClientMethod(code = ServiceCode.AtvAnswerQuestionManager_showView)
	public void showView(){
 		long teamId = getTeamId();
//		Team team = teamManager.getTeam(teamId);
//        if (isModuleDisable(team)) {
//            sendMessage(ErrorCode.Team_Level);
//            return;
//        }
		
		AtvAnswerQuestionPB.AtvAnswerQuestionData.Builder builder =
				AtvAnswerQuestionPB.AtvAnswerQuestionData.newBuilder();
		int type = 0;
		if ((type = getOpenTimeType()) == 0) {
			builder.setState(ATV_END);
			sendMessage(builder.build());
			return;
		}
		
		AnswerQuestionData obj = redis.getObj(RedisKey.getAtcAnswerQuestionKey(teamId));
		if (obj == null) {
			obj = initAnswerQuestionData();
			if (obj == null) {
				sendMessage(ErrorCode.ParamError);
				log.warn("AtcAnswerQuestion in showView() config null!");
				return;
			}
			
			redis.set(RedisKey.getAtcAnswerQuestionKey(teamId), obj, RedisKey.DAY);
		}
		
		// 如果第二次答题开始,玩家没有答过题,则设置答题状态为未开始答题
		if (type == ATV_SECOND_TYPE && obj.getAnswerMap().get(type).size() == 0) {
			obj.setState(ATV_NOT_START);
		}
		
		if (obj.getState() != ATV_END) {
			for (Integer qId : obj.getQuestionMap().get(type)) {
				AtvQuestionData.Builder builder2 = getAtvQuestionDataBuilder(qId);
				builder.addAtvQuestionList(builder2.build());
			}
		}
		
		builder.setState(obj.getState());
		builder.setFinishCount(obj.getAnswerMap().get(type).size());
		
		sendMessage(builder.build());
	}
	
	/**
	 * 答题
	 * @param id		当前题目唯一Id
	 * @param answer	回答的答案序号
	 */
	@ClientMethod(code = ServiceCode.AtvAnswerQuestionManager_answer)
	public void answerQuestion(int id, int answer){
		int type = getOpenTimeType();
		if (type == 0) {
			sendMessage(ErrorCode.Active_1);
			return;
		}
		
		AnswerQuestionBeanVO beanVO = AnswerQuestionConsole.getAnswerQuestionBeanVO(id);
		if (beanVO == null) {
			sendMessage(ErrorCode.ParamError);
			return;
		}
		
		long teamId = getTeamId();
		Team team = teamManager.getTeam(teamId);
		AnswerQuestionData objData = redis.getObj(RedisKey.getAtcAnswerQuestionKey(teamId));
		// 玩家答题的答案记录
		Map<Integer, Map<Integer, Integer>> map = objData.getAnswerMap();
		
		if (map.get(type).get(id) != null) {
			//玩家已经回答过此题
			sendMessage(ErrorCode.OtherError);
			return;
		}
		
		GlobalBean global = ConfigConsole.getGlobal();
		int exp = 0;
		List<DropBean> dropList = null;
		List<PropSimple> awardList = Lists.newArrayList();
		
		boolean correct = false;
		//判断答题是否正确
		if (beanVO.getAnswerId() == answer) {
			exp = global.answerWinExp * team.getLevel();
			dropList = answerWinDropList;
			correct = true;
		}else {//答题错误
			exp = global.answerLossExp * team.getLevel();
			dropList = answerLossDropList;
		}
		
		List<Integer> questionList = objData.getQuestionMap().get(type);
		// 如果玩家答的题目是最后一道则设置玩家的答题活动状态为结束状态
		if (questionList != null && id == questionList.get(questionList.size() - 1)) {
			objData.setState((short)ATV_END);
		}
		
		TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
		teamMoneyManager.update(tm, EMoneyType.Exp, exp, module);
		dropList.forEach(obj -> {
			List<PropSimple> psList = obj.roll();
			if (psList.size() > 0) {
				awardList.add(psList.get(0));
			}
		});
		
		
		propManager.addPropList(teamId, awardList, true, 
			ModuleLog.getModuleLog(EModuleCode.AtcAnswerQuestion, "答题活动获得"));
		//发给前端经验
		awardList.add(new PropSimple(4002, exp));
		List<PropPB.PropSimpleData> propLists = PropManager.getPropSimpleListData(awardList);
		map.get(type).put(id, answer);
		redis.set(RedisKey.getAtcAnswerQuestionKey(teamId), objData, RedisKey.DAY);
		sendMessage(AtvAnswerQuestionPB.AtvAnswerQuestionResp.newBuilder()
			.setCorrect(correct).setFinishCount(map.get(type).size())
			.addAllAwardList(propLists).build());
	}
	
	/**
	 * 修改答题活动的状态:1开始答题,2结束答题.
	 * @param status
	 */
	@ClientMethod(code = ServiceCode.AtvAnswerQuestionManager_updateAtvState)
	public void updateAtvState(int status){
		int type = getOpenTimeType();
		if (type == 0) {
			sendMessage(ErrorCode.Active_1);
			return;
		}
		
		long teamId = getTeamId();
		AnswerQuestionData objData = redis.getObj(RedisKey.getAtcAnswerQuestionKey(teamId));
		objData.setState((short)status);
		redis.set(RedisKey.getAtcAnswerQuestionKey(teamId), objData);
		sendMessage(ErrorCode.Success);
	}
	
	/** 答题活动在玩家达到固定等级后开启 */
    private boolean isModuleDisable(Team team) {
        return ModuleConsole.isDisabled(team.getLevel(), EModuleCode.AtcAnswerQuestion);
    }
	
	/**
	 * 从题库中随机不重复的题目.
	 * @param num	随机题目的数量.
	 * @return		随机出的题目.
	 */
	private List<Integer> getRandomQuestion(int num){
		List<Integer> questionIdList = AnswerQuestionConsole.getQuestionIdList();
		
		List<Integer> tmpList = new ArrayList<Integer>(questionIdList);
		Collections.shuffle(tmpList);
		if (tmpList.size() < num) {
			return tmpList;
		}
		
		return tmpList.subList(0, num);
	}
	
	/**
	 * 获得答题活动开放的时间类型:1(11:00 - 11:30)|2(20:00 - 20:30),
	 * 不在开放时间内则返回0.
	 * @return
	 */
	private int getOpenTimeType(){
		int type = 0;
		DateTime dateTime = new DateTime();
		int currSeconds = dateTime.getHourOfDay() * 60 + dateTime.getMinuteOfHour();
		if ( FIRST_OPEN_TIME[0] <= currSeconds && currSeconds <= FIRST_OPEN_TIME[1]) {
			type  = ATV_FIRST_TYPE;
		}else if (SECOND_OPEN_TIME[0] <= currSeconds && currSeconds <= SECOND_OPEN_TIME[1]) {
			type = ATV_SECOND_TYPE;
		}
		
		return type;
	}
	
	private AtvAnswerQuestionPB.AtvQuestionData.Builder getAtvQuestionDataBuilder(int questionId){
		AtvAnswerQuestionPB.AtvQuestionData.Builder builder = AtvAnswerQuestionPB.AtvQuestionData.newBuilder();
		AnswerQuestionBeanVO beanVO = AnswerQuestionConsole.getAnswerQuestionBeanVO(questionId);
		builder.setAnswer(beanVO.getAnswerId());
		builder.setContent(beanVO.getQuestionTitle());
		builder.setId(beanVO.getQuestionId());
		builder.addOptionList(beanVO.getSelection1());
		builder.addOptionList(beanVO.getSelection2());
		builder.addOptionList(beanVO.getSelection3());
		builder.addOptionList(beanVO.getSelection4());
		return builder;
	}



	@Override
	public void offline(long teamId) {
		
	}

	@Override
	public void dataGC(long teamId) {
		
	}
	
}
