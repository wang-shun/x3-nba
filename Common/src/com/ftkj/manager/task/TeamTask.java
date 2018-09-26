package com.ftkj.manager.task;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.console.TaskConsole;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年6月8日
 * 球队任务
 */
public class TeamTask {
	/** 主线任务 包括成就任务*/
	private List<Task> trunkList;
    /** 日常任务*/
	private TaskDay taskDay;
	/** 活动任务*/
	private Map<Integer, TaskActive> taskActiveMap;
	
	public TeamTask() {
	    this.trunkList = Lists.newArrayList();
        this.taskDay = new TaskDay();
        this.taskActiveMap = Maps.newHashMap();
    }
	
	public TeamTask(List<Task> trunkList,TaskDay taskDay) {
		super();
		this.trunkList = trunkList;
		this.taskDay = taskDay;
		this.taskActiveMap = Maps.newHashMap();
	}

	public Task getTask(int tid){
		Task t = this.trunkList.stream().filter(task->task.getTid()==tid).findFirst().orElse(null);
		if(t == null){
			t = this.taskDay.getDayTask(tid);
		}
		if(t == null && this.taskActiveMap.containsKey(tid)) {
			t = this.taskActiveMap.get(tid).getTask();
		}
		return t;
	}
	
    public void setTrunkList(List<Task> trunkList) {
        this.trunkList = trunkList;
    }

    public void setTaskDay(TaskDay taskDay) {
        this.taskDay = taskDay;
    }
	
	public TaskDay getTaskDay() {
		return taskDay;
	}

	public void updateDayTask(List<Task> newList){
		Map<Integer,Task> tmp = newList.stream().collect(Collectors.toMap(task->task.getTid(), k->k));
		if(this.taskDay == null) {
		    this.taskDay = new TaskDay();
		}
		this.taskDay.setDayMap(tmp);
	}
	
	public void replaceDayTask(Task oldTask,Task newTask){
	    TaskBean taskBean = TaskConsole.getTaskBean(oldTask.getTid());
        if (taskBean == null) return;
                 
        if(oldTask.getStatus() == EStatus.TaskClose.getId() && taskBean.getDayType() != 1) {
            this.taskDay.getDayMap().remove(oldTask.getTid());
        }        
		
		this.taskDay.getDayMap().put(newTask.getTid(), newTask);
	}
	
	/**
	 * 取所有保存的任务
	 * @return
	 */
	public List<Task> getTrunkList(ETaskType type) {
		if(type == ETaskType.所有任务) {
			return trunkList;
		}
		
		return trunkList.stream().filter(t-> TaskConsole.getTaskBean(t.getTid()) != null && TaskConsole.getTaskBean(t.getTid()).getType() == type).collect(Collectors.toList());
	}
	
	public Map<Integer, Task> getDayMap() {
		return this.taskDay.getDayMap();
	}
	public String getTaskVersion() {
	    if(this.taskDay == null) {
	        return "";
	    }
		return this.taskDay.getTaskVersion();
	}
	public void setTaskVersion(String taskVersion) {
	    if(taskDay == null){
	        return;
        }
		this.taskDay.setTaskVersion(taskVersion);
	}
	
	public List<Task> getActiveTaskList() {
		DateTime now = DateTime.now();
		return this.taskActiveMap.values().stream().filter(s-> s.getEndTime().isAfter(now)).map(t-> t.getTask()).collect(Collectors.toList());
	}

	public Map<Integer, TaskActive> getTaskActiveMap() {
		return taskActiveMap;
	}

	public void setTaskActiveMap(Map<Integer, TaskActive> taskActiveMap) {
		this.taskActiveMap = taskActiveMap;
	}
	
	public void clearActiveTask(){
		DateTime now = DateTime.now();
		Iterator<Entry<Integer, TaskActive>> it = this.taskActiveMap.entrySet().iterator();  
		while(it.hasNext()){ 
			 Entry<Integer, TaskActive> entry = it.next();  
	         int key = entry.getKey();  
	         if(!this.taskActiveMap.containsKey(key)) {
				continue;
			 }
	         if(entry.getValue().getEndTime().isBefore(now)) {
	        	 it.remove();
	         }
		}
	}	
	  
    public void clearTaskDay() {
        if(this.taskDay == null) {
            return;
        }
        this.taskDay.clearTaskDay();
    }
}
