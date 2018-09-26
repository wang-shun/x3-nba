package com.ftkj.manager.active.data;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;

/**
 * 每日可领
 * @author Jay
 * @time:2018年2月26日 下午6:32:45
 */
public class AtvDayAwardData extends ActiveBase {
	
	private static final long serialVersionUID = 1L;
	
//	private int value; //idata1
//	private int maxDay; //idata2
//	private int getDay; //idata3
//	private String lastGetDate; //sdata1
	
	public AtvDayAwardData(ActiveBasePO po) {
		super(po);
	}
	
	public int getValue() {
		return this.getiData1();
	}
	public void setValue(int value) {
		this.setiData1(value);
	}
	public int getMaxDay() {
		return this.getiData2();
	}
	public void setMaxDay(int maxDay) {
		this.setiData2(maxDay);
	}
	/**
	 * 已领取天数
	 * @return
	 */
	public int getGetDay() {
		return this.getiData3();
	}
	/**
	 * 已领取加1天
	 */
	public void addGetDay() {
		this.setiData3(this.getiData3()+1);
	}
	public String getLastGetDate() {
		return this.getsData1();
	}
	public void setLastGetDate(String lastGetDate) {
		this.setsData1(lastGetDate);
	}
	
}
