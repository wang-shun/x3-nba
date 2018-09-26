package com.ftkj.manager.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;


/**
 * 日常任务
 * @author tim.huang
 * 2017年6月29日
 *
 */
public class TaskDay implements Serializable {
    private static final long serialVersionUID = -1569397484555593951L;
    
    private Map<Integer,Task> dayMap = Maps.newConcurrentMap();  
	/** 任务版本*/
	private String taskVersion;
	/** 已领取的星级奖励ID*/
	private List<Integer> status = new ArrayList<>();
	/** 日常任务星数*/
	private int taskStarSum;	
	
	public String getTaskVersion() {
		return taskVersion;
	}
	public void setTaskVersion(String taskVersion) {
		this.taskVersion = taskVersion;
	}
	
	public Task getDayTask(int tid){
		return this.dayMap.get(tid);
	}
	
	public Map<Integer, Task> getDayMap() {
		return dayMap;
	}
	public void setDayMap(Map<Integer, Task> dayMap) {
		this.dayMap = dayMap;
	}
    public int getTaskStarSum() {
        return taskStarSum;
    }

    public void setTaskStarSum(int star) {
        this.taskStarSum = taskStarSum + star;
    }   

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }    
    
    public void clearTaskDay() {
        this.status.clear();
        this.taskStarSum = 0;    
        this.dayMap.clear();
    }	
    
    @Override
    public String toString() {
        
        String str = null;
        for(Map.Entry<Integer,Task> entry : dayMap.entrySet()) {
            str = str + entry.getValue().toString();
        }
        
        return "{" +
            "\"taskVersion\":" + taskVersion +
            ", \"taskStarSum\":" + taskStarSum +
            ", \"dayMap\":" + str +          
            '}';
    }
}
