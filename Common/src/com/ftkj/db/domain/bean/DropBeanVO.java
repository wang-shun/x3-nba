package com.ftkj.db.domain.bean;

/**
 * @author tim.huang
 * 2016年3月17日
 * 掉落配置数据[DB数据]
 */
public class DropBeanVO {
	/**
	 * 掉落ID
	 */
	private int dropId;
	/**
	 * 掉落类型
	 */
	private int dropType;
	/**
	 * 物品ID
	 */
	private int propId;
	/**
	 * 最小数量
	 */
	private int min;
	/**
	 * 最大数量
	 */
	private int max;
	/**
	 * 掉落概率
	 */          
	private int probaility;
	/**
	 * 备注
	 */
	private String remark;
	public int getDropId() {
		return dropId;
	}
	public int getProbaility() {
		return probaility;
	}
	public String getRemark() {
		return remark;
	}
	public void setDropId(int dropId) {
		this.dropId = dropId;
	}
	public void setPropId(int propId) {
		this.propId = propId;
	}
	public void setProbaility(int probaility) {
		this.probaility = probaility;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getDropType() {
		return dropType;
	}
	public void setDropType(int dropType) {
		this.dropType = dropType;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getPropId() {
		return propId;
	}
	
	
	
	
}
