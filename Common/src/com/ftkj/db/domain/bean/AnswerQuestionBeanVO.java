package com.ftkj.db.domain.bean;


/**
 * 答题活动配置数据
 * @author mr.lei
 * 2018年9月13日12:09:24
 *
 */
public class AnswerQuestionBeanVO {
	/**题目Id*/
	private int questionId;
	/**题目内容*/
	private String questionTitle;
	/**题目选项1*/
	private String selection1;
	/**题目选项2*/
	private String selection2;
	/**题目选项3*/
	private String selection3;
	/**题目选项4*/
	private String selection4;
	
	/**正确答案序号*/
	private int answerId;
	
	
	public AnswerQuestionBeanVO(){
		
	}


	public int getQuestionId() {
		return questionId;
	}


	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}


	public String getQuestionTitle() {
		return questionTitle;
	}


	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}


	public String getSelection1() {
		return selection1;
	}


	public void setSelection1(String selection1) {
		this.selection1 = selection1;
	}


	public String getSelection2() {
		return selection2;
	}


	public void setSelection2(String selection2) {
		this.selection2 = selection2;
	}


	public String getSelection3() {
		return selection3;
	}


	public void setSelection3(String selection3) {
		this.selection3 = selection3;
	}


	public String getSelection4() {
		return selection4;
	}


	public void setSelection4(String selection4) {
		this.selection4 = selection4;
	}


	public int getAnswerId() {
		return answerId;
	}

	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}
	
}
