package com.ftkj.manager.buff;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;

public class TeamBuff implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 加成的键值：如，联盟加成，球队加成，月卡加成，VIP加成等。
	 */
	private EBuffKey key;
	/**
	 * 加成类型，如：工资帽
	 */
	private EBuffType type;
	
	private DBList values;
	/**
	 * 是否覆盖：
	 * true 直接覆盖
	 * false 时间叠加
	 */
	private boolean isOverlay;
	private DateTime endTime;
	
	public TeamBuff(EBuffKey key, EBuffType type, int[] values, DateTime endTime, boolean isOverlay) {
		super();
		this.key = key;
		this.type = type;
		this.values = new DBList(values);
		this.endTime = endTime;
		this.isOverlay = isOverlay;
	}
	
	public TeamBuff(Buff buff) {
		this.key = EBuffKey.valueOfKey(buff.getId()/100*100);
		this.type = EBuffType.valueOfKey(buff.getId() % 100);
		this.values = new DBList(buff.getParams());
		this.endTime = buff.getEndTime();
		this.isOverlay = false;
	}

	public EBuffType getType() {
		return type;
	}
	public void setType(EBuffType type) {
		this.type = type;
	}
	
	public DateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public boolean isOverlay() {
		return isOverlay;
	}

	public void setOverlay(boolean isOverlay) {
		this.isOverlay = isOverlay;
	}

	public EBuffKey getKey() {
		return key;
	}

	public void setKey(EBuffKey key) {
		this.key = key;
	}
	
	public DBList getValues() {
		return values;
	}

	public void setValues(DBList values) {
		this.values = values;
	}

	/**
	 * Buff的唯一ID
	 * @return
	 */
	public Integer getBuffID() {
		return this.getType().getId() + this.getKey().getStartID();
	}
}
