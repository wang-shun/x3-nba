package com.ftkj.manager.active.starload;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;

/**
 * 巨星之路通用
 * @author Jay
 * @time:2017年9月8日 下午2:49:19
 */
public class AtvPlayerStarRoad extends ActiveBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AtvPlayerStarRoad(ActiveBasePO po) {
		super(po);
	}
	
	/**
	 * 领取状态，号分割：
	 * 格式--： 0,0,0,0,0,0,0,...
	 * 意义: 0条件未达成   1条件达成    2已领取奖励
	 */
	@ActiveDataField(fieldName = "sData1", size = 17)
	private DBList status;

	public DBList getStatus() {
		return status;
	}
	
	/**
	 * 达到目标完成时间，排名用
	 * @param time
	 */
	public void setFinishTime(DateTime time) {
		this.setLastTime(time);
	}
	
	public DateTime getFinishTime() {
		return this.getLastTime();
	}
	
	/**
	 * 记录数量
	 * @param value
	 */
	public void setCount(int value) {
		this.setiData1(value);
	}
	
	public int getCount() {
		return this.getiData1();
	}
	
	/**
	 * 排名条件
	 */
	public void setRankValue(int value) {
		this.setiData2(value);
	}
	
	public int getRankValue() {
		return this.getiData2();
	}
}
