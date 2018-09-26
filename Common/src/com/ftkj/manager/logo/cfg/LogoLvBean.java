package com.ftkj.manager.logo.cfg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.util.excel.RowData;

/**
 * 荣誉头像等级配置项
 * @author Jay
 * @time:2017年3月16日 上午11:36:52
 */
public class LogoLvBean {

	/**
	 * 当前等级
	 */
	private int lv;
	/**
	 * 等级命名
	 */
	private String name;
	/**
	 * 单次消耗球券
	 */
	private int fk;
	/**
	 * 单次消耗建设费
	 */
	private int jsf;
	/**
	 * 转移需要卡数量
	 */
	private int cardNum;
	/**
	 * 小星脉个数
	 */
	private int step;
	/**
	 * 成功率
	 */
	private float stepRate;
	/**
	 * 大星脉个数
	 */
	private int stat;
	/**
	 * 成功率
	 */
	private float starRate;
	/**
	 * 星脉等级攻防
	 */
	private List<Integer> starCap;
	/**
	 * 等级累计攻防
	 */
	private int cap;
	
	/**
	 * 自解析
	 * @param row
	 */
	public void initExec(RowData row) {
		int[] starAdd = {
				row.get("star1"),
				row.get("star2"),
				row.get("star3"),
				row.get("star4"),
				row.get("star5")
		};
		this.starCap = Arrays.stream(starAdd).boxed().collect(Collectors.toList());
	}
	
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStat() {
		return stat;
	}

	public void setStat(int stat) {
		this.stat = stat;
	}

	public int getLv() {
		return lv;
	}
	public void setLv(int lv) {
		this.lv = lv;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFk() {
		return fk;
	}
	public void setFk(int fk) {
		this.fk = fk;
	}
	public int getJsf() {
		return jsf;
	}
	public void setJsf(int jsf) {
		this.jsf = jsf;
	}
	public int getCardNum() {
		return cardNum;
	}
	public void setCardNum(int cardNum) {
		this.cardNum = cardNum;
	}
	public float getStepRate() {
		return stepRate;
	}
	public void setStepRate(float stepRate) {
		this.stepRate = stepRate;
	}
	public float getStarRate() {
		return starRate;
	}
	public void setStarRate(float starRate) {
		this.starRate = starRate;
	}
	public List<Integer> getStarCap() {
		return starCap;
	}
	public void setStarCap(List<Integer> starCap) {
		this.starCap = starCap;
	}
	public int getCap() {
		return cap;
	}
	public void setCap(int cap) {
		this.cap = cap;
	}
	
}
