package com.ftkj.manager.task;

import com.ftkj.db.domain.bean.TaskConditionBeanVO;
import com.ftkj.enums.ETaskCondition;

/**
 * @author tim.huang
 * 2017年6月7日
 * 任务条件
 */
public class TaskConditionBean {
    
	/** 任务类型*/
	private ETaskCondition condition;	
	/** 任务状态检测接口*/
    private ITaskFinish finish;
	/** 更新条件的时候是直接set还是自增*/
	private int isSetStr;
	/** 更新的条件值*/
	private String valString;
	/** 更新条件的时候是直接set还是自增*/
	private int isSetInt;
	/** 更新的条件值*/
	private int valInt;	
	
	public TaskConditionBean(TaskConditionBeanVO vo,ITaskFinish finish){
		this.condition = ETaskCondition.getETaskCondition(vo.getCid());		
		this.setFinish(finish);
		this.isSetStr = vo.getStrSet();
		this.valString = vo.getValStr();
		this.isSetInt = vo.getIntSet();
		this.valInt = vo.getValInt();
	}
	
	public ETaskCondition getCondition() {
		return condition;
	}

	public int getIsSetStr() {
		return isSetStr;
	}
	public String getValString() {
		return valString;
	}
	public int getIsSetInt() {
		return isSetInt;
	}
	public int getValInt() {
		return valInt;
	}

	@Override
	public String toString() {
		return "TaskConditionBean [condition=" + condition + ", isSetStr=" + isSetStr + ", valString=" + valString
				+ ", isSetInt=" + isSetInt + ", valInt=" + valInt + "]";
	}

    public ITaskFinish getFinish() {
        return finish;
    }

    public void setFinish(ITaskFinish finish) {
        this.finish = finish;
    }
	
}
