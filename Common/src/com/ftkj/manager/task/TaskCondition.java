package com.ftkj.manager.task;

import java.io.Serializable;

import com.ftkj.db.domain.TaskConditionPO;
import com.ftkj.enums.EStatus;

public class TaskCondition implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private TaskConditionPO condition;
	
    public TaskCondition(TaskConditionPO condition) {
        super();
        this.condition = condition;
    }
    
	/** 更新任务条件值*/
	public void updateVal(int valInt,String valStr, int isSetInt, int isSetStr, boolean trunk){
	
		if(isSetInt == EStatus.True.getId()){
		    this.condition.setValInt(valInt);
        }else {
            this.condition.setValInt(this.getValInt() + valInt);
        }
		
	    if(isSetStr == EStatus.True.getId()){
            this.condition.setValStr(valStr);
        }else {
            this.condition.setValStr(valStr + this.getValStr());
        }        
        
        if(trunk) {
            this.condition.save();
        }
	}
	
	public void save(){
		this.condition.save();
	}
	
	public int getTid(){
		return this.condition.getTid();
	}

	public int getCid(){
		return this.condition.getCid();
	}
	
	public int getValInt(){
		return this.condition.getValInt();
	}
	
	public String getValStr(){
		return this.condition.getValStr();
	}

    public void setCondition(TaskConditionPO condition) {
        this.condition = condition;
    }
    
    public TaskConditionPO getCondition() {
        return condition;
    }
    

	@Override
	public String toString() {
		return "TaskCondition [condition=" + condition + "]";
	}	
}
