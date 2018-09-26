package com.ftkj.event.param;

import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ETaskType;

public class TaskParam {
	
	public long teamId;
	public int taskId;
	public ETaskType type;
	public ETaskCondition conditoin;
	public int val;
	public int status;
	public TaskParam(long teamId, int taskId, ETaskType type, ETaskCondition conditoin, int val, int status) {
		super();
		this.teamId = teamId;
		this.taskId = taskId;
		this.type = type;
		this.conditoin = conditoin;
		this.val = val;
		this.status = status;
	}
	
}
