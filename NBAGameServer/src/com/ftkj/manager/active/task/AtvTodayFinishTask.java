package com.ftkj.manager.active.task;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;

/**
 * 每天活动数据类
 */
public class AtvTodayFinishTask extends ActiveBase {
	
	private static final long serialVersionUID = 1L;
	public AtvTodayFinishTask(ActiveBasePO po) {
		super(po);
	}
	
	@ActiveDataField(fieldName="sData1", size=7)
	private DBList taskPlan;
	
	/** 点燃状态，是否已领今天奖励 */
	public void setTodayAwardStatus(int status) {
		this.setiData1(status);
	}
	
	/** 点燃状态，是否已领今天奖励 0否，1已领 */
	public int getTodayAwardStatus() {
		return this.getiData1();
	}

	/**
	 * 任务进度
	 * @return
	 */
	public DBList getTaskPlan() {
		return taskPlan;
	}

    public void setTaskPlan(DBList taskPlan) {
        this.taskPlan = taskPlan;
    }
}
