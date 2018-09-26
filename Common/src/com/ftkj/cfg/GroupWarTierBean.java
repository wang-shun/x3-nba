package com.ftkj.cfg;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 组队赛段位配置
 * @author lin.lin
 */
public class GroupWarTierBean extends ExcelBean {

	private int id;
	private int minRating;
	private int nextRating;
	private int limitCount;
	private int ratingFactor;
	private float failCorrectionFactor;
	// 奖励
	private int eachHonor; // 每场荣誉
	private List<PropSimple> eachAwardList;
	private List<PropSimple> dayAwardList;
	private List<PropSimple> seasonAwardList;
	
	@Override
	public void initExec(RowData row) {
		eachAwardList = PropSimple.getPropBeanByStringNotConfig(row.get("eachAward"));
		dayAwardList = PropSimple.getPropBeanByStringNotConfig(row.get("dayAward"));
		seasonAwardList = PropSimple.getPropBeanByStringNotConfig(row.get("seasonAward"));
	}
	
	//
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMinRating() {
		return minRating;
	}
	public void setMinRating(int minRating) {
		this.minRating = minRating;
	}
	public int getRatingFactor() {
		return ratingFactor;
	}
	public void setRatingFactor(int ratingFactor) {
		this.ratingFactor = ratingFactor;
	}
	public float getFailCorrectionFactor() {
		return failCorrectionFactor;
	}
	public void setFailCorrectionFactor(float failCorrectionFactor) {
		this.failCorrectionFactor = failCorrectionFactor;
	}
	public int getNextRating() {
		return nextRating;
	}
	public void setNextRating(int nextRating) {
		this.nextRating = nextRating;
	}
	public int getLimitCount() {
		return limitCount;
	}
	public void setLimitCount(int limitCount) {
		this.limitCount = limitCount;
	}

	public int getEachHonor() {
		return eachHonor;
	}

	public void setEachHonor(int eachHonor) {
		this.eachHonor = eachHonor;
	}

	public List<PropSimple> getEachAwardList() {
		return eachAwardList;
	}

	public void setEachAwardList(List<PropSimple> eachAwardList) {
		this.eachAwardList = eachAwardList;
	}

	public List<PropSimple> getDayAwardList() {
		return dayAwardList;
	}

	public void setDayAwardList(List<PropSimple> dayAwardList) {
		this.dayAwardList = dayAwardList;
	}

	public List<PropSimple> getSeasonAwardList() {
		return seasonAwardList;
	}

	public void setSeasonAwardList(List<PropSimple> seasonAwardList) {
		this.seasonAwardList = seasonAwardList;
	}
	
}
