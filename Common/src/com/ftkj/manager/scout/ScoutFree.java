package com.ftkj.manager.scout;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.ftkj.util.DateTimeUtil;

/**
 * @author tim.huang
 * 2017年10月30日
 *
 */
public class ScoutFree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DateTime level1;
	private DateTime level2;
	private DateTime level3;
	
	private static final int l1 = 12;
	private static final int l2 = 24;
	private static final int l3 = 48;//免费招募的CD时间，单位小时
	
	public ScoutFree() {
		updateLevel(1);
		updateLevel(2);
		updateLevel(3);
	}

	public int getSecond(int type){
		DateTime end = null;
		if(type == 1){
			end = this.level1==null?updateLevel(type):this.level1;
		}else if(type == 2){
			end = this.level2==null?updateLevel(type):this.level2;
		}else if(type == 3 || type == 4){
			end = this.level3==null?updateLevel(type):this.level3;
		}
		
		return DateTimeUtil.secondBetween(DateTime.now(), end);
	}
	
	public DateTime updateLevel(int type){
		if(type == 1){
			this.level1 = DateTime.now().plusHours(l1);
			return this.level1;
		}else if(type == 2){
			this.level2 = DateTime.now().plusHours(l2);
			return this.level2;
		}else if(type == 3 || type == 4){
			this.level3 = DateTime.now().plusHours(l3);
			return this.level3;
		}
		return this.level3;
	}

}
