package com.ftkj.manager.answer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

/**
 * 玩家参加答题活动的保存数据.
 * @author mr.lei
 * @since 2018年9月14日14:22:48
 */
public class AnswerQuestionData implements Serializable {

	private static final long serialVersionUID = 1L;
	/**玩家开始答题时间*/
	private DateTime startDateTime;
	/**随机的题目,key=类型,1|2,value=题目Id*/
	private Map<Integer, List<Integer>> questionMap;
	/**玩家答题记录,key=类型,1|2,value=(key=题目Id,value=玩家回答的答案)*/
	private Map<Integer, Map<Integer, Integer>> answerMap;
	/**答题的状态:0未开始,1开始答题,2答题结束*/
	private short state = 0;
	
	public AnswerQuestionData() {
	
	}
	

	public AnswerQuestionData(Map<Integer, List<Integer>> questionMap,
			Map<Integer, Map<Integer, Integer>> answerMap) {
		super();
		this.questionMap = questionMap;
		this.answerMap = answerMap;
	}



	public DateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(DateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Map<Integer, List<Integer>> getQuestionMap() {
		return questionMap;
	}

	public void setQuestionMap(Map<Integer, List<Integer>> questionMap) {
		this.questionMap = questionMap;
	}

	public Map<Integer, Map<Integer, Integer>> getAnswerMap() {
		return answerMap;
	}

	public void setAnswerMap(Map<Integer, Map<Integer, Integer>> answerMap) {
		this.answerMap = answerMap;
	}


	public short getState() {
		return state;
	}


	public void setState(short state) {
		this.state = state;
	}
	
	
}
