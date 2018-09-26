package com.ftkj.manager.cdkey;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.excel.ExcelConfigBean;
import com.ftkj.util.excel.RowData;


/**
 * @author tim.huang
 * 2016年12月16日
 * 激活码配置类
 */
public class ConverCodeBean extends ExcelBean{
	private String id;
	private String plat;
	private List<PropSimple> drop;
	private int maxCount;
	private int dayCount;
	private int only;
	private DateTime startTime;
	private DateTime endTime;
	
	public ConverCodeBean() {
	}
	
	
	@Override
	public void initExec(RowData row) {
		String props = row.get("props");
		this.drop = PropSimple.getPropBeanByStringNotConfig(props);
		this.startTime = DateTimeUtil.getDateTime(row.get("start_time"));
		this.endTime = DateTimeUtil.getDateTime(row.get("end_time"));
	}


	public boolean isStart(){
		Interval i = new Interval(this.startTime, this.endTime);  
		DateTime now = DateTime.now();
		return  i.contains(now);
	}
	
	public void setDrop(List<PropSimple> drop) {
		this.drop = drop;
	}

	public int getOnly() {
		return only;
	}
	public String getPlat() {
		return plat;
	}
	public List<PropSimple>  getDrop() {
		return drop;
	}
	public String getId() {
		return id;
	}
	public int getMaxCount() {
		return this.maxCount;
	}
	public int getDayCount() {
		return dayCount;
	}
	public DateTime getStartTime() {
		return startTime;
	}
	public DateTime getEndTime() {
		return endTime;
	}
	
}