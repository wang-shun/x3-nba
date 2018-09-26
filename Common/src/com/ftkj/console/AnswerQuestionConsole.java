package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.map.UnmodifiableMap;

import com.ftkj.cfg.EmailViewBean;
import com.ftkj.db.domain.bean.AnswerQuestionBeanVO;

public class AnswerQuestionConsole {

	private static Map<Integer,AnswerQuestionBeanVO> viewMap;
	
	private static List<Integer> answerQuestionBeanVOList;
	
	/**
	 * 初始化
	 */
	public static void init(List<AnswerQuestionBeanVO> list) {
		viewMap = UnmodifiableMap.unmodifiableMap(
			list.stream().collect(Collectors.toMap(AnswerQuestionBeanVO::getQuestionId, (e) -> e)));
		answerQuestionBeanVOList = UnmodifiableList.unmodifiableList(
			list.stream().map(AnswerQuestionBeanVO::getQuestionId).collect(Collectors.toList()));
	}
	
	public static AnswerQuestionBeanVO getAnswerQuestionBeanVO(int id) {
		return viewMap.get(id);
	}

	/**
	 * 获得活动答题题库所有题目的Id.
	 * @return
	 */
	public static List<Integer> getQuestionIdList() {
		return answerQuestionBeanVOList;
	}
	
}
