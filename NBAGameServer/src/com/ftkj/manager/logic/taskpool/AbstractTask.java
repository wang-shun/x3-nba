package com.ftkj.manager.logic.taskpool;

import java.io.Serializable;

import com.ftkj.manager.BaseManager;
import com.ftkj.manager.task.TaskCondition;
import com.ftkj.manager.task.TaskConditionBean;
import com.ftkj.server.instance.InstanceFactory;

/**
 * 任务抽象类
 * @author ken
 * @date 2017-2-21
 */
public abstract class AbstractTask implements Serializable {

	private static final long serialVersionUID = 7823607801642129236L;
	
	protected <T extends BaseManager> T getManager(Class<T> cla){
        return InstanceFactory.get().getInstance(cla);
    }
	
	/** 接收任务*/
    public abstract boolean acceptTask(long teamId, TaskConditionBean taskConditionBean, TaskCondition taskCondition);

	/** 检测任务条件是否达成*/
	public abstract boolean executeTask(long teamId, TaskConditionBean taskConditionBean, TaskCondition taskCondition);

}
