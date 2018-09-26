package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * @author tim.huang
 * 2017年9月26日
 *
 */
public class SkillLevelBean extends ExcelBean {

	private int step;
	private int level;
	private int rate;
	private int failAdd;
	private int maxRate;
	private int num;
	private int needPropId;
	
	private int s1;
	private int s2;
	private int s3;
	
	public int getS1() {
		return s1;
	}

	public int getS2() {
		return s2;
	}

	public int getS3() {
		return s3;
	}

	public int getNeedPropId() {
		return needPropId;
	}

	public int getNum() {
		return num;
	}

	public int getStep() {
		return step;
	}

	public int getLevel() {
		return level;
	}

	public int getRate() {
		return rate;
	}

	public int getFailAdd() {
		return failAdd;
	}

	public int getMaxRate() {
		return maxRate;
	}

	@Override
	public void initExec(RowData row) {

	}

}
