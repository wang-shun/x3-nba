package com.ftkj.manager.task;

import java.io.Serializable;
import java.util.List;

import com.ftkj.db.domain.TaskPO;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskType;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年6月8日
 *
 */
public class Task implements Serializable{	

    private static final long serialVersionUID = 1L;
	
	/** 任务基本信息	 */
	private TaskPO task;
	/** 任务的条件达成信息 */
	private List<TaskCondition> conditionList;	

	public Task(TaskPO task) {
		super();
		this.task = task;
		this.conditionList = Lists.newArrayList();
	}
	
   public Task() {
        super();
        this.task = new TaskPO();
        this.conditionList = Lists.newArrayList();
    }

	public Task(TaskPO task, List<TaskCondition> conditionList) {
		this(task);
		if(conditionList != null) {
			this.conditionList = conditionList;
		}
	}

	public int getTid(){
		return this.task.getTid();
	}
	
	public List<TaskCondition> getConditionList() {
		return conditionList;
	}
	
	public int getStatus(){
		return this.task.getStatus();
	}
	
	public void updateStatus(EStatus status,ETaskType taskType){
		this.task.setStatus(status.getId());
		if(taskType.isSaveDb()) {
			this.task.save();
		}
	}
	
	private void saveConditionList(){
		this.conditionList.forEach(con->con.save());
	}

    public void setTask(TaskPO task) {
        this.task = task;
    }

    public void setConditionList(List<TaskCondition> conditionList) {
        this.conditionList = conditionList;
    }
    
    public void save() {
    	this.task.save();
    	this.saveConditionList();
    }

	@Override
	public String toString() {
		return "Task [taskId=" + task.getTid() + ", status=" + task.getStatus() + ", " + conditionList + "]";
	}
	
}
