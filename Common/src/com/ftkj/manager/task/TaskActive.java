package com.ftkj.manager.task;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.ftkj.util.DateTimeUtil;

/**
 * 任务活动
 * @author Jay
 * @time:2018年3月27日 下午2:41:45
 */
public class TaskActive implements Serializable {

    private static final long serialVersionUID = 1021791477189301152L;
    
    private int atvId;
	private DateTime endTime;
	private Task task;
	
	public TaskActive(int atvId, DateTime endTime, Task task) {
		super();
		this.atvId = atvId;
		this.endTime = endTime;
		this.task = task;
	}

	public int getAtvId() {
		return atvId;
	}

	public void setAtvId(int atvId) {
		this.atvId = atvId;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public String toString() {
		return "TaskActive [atvId=" + atvId + ", endTime=" + DateTimeUtil.getStringSql(endTime) + ", task=" + task + "]";
	}	
}
