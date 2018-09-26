package com.ftkj.manager.logic.taskpool;

import com.ftkj.manager.task.TaskCondition;
import com.ftkj.manager.task.TaskConditionBean;

/**
 * 任务类型10,属于成就任务:装备升阶.
 * 满足多少件装备进阶到什么品质就完成一个成就任务.
 * @author mr.lei
 *
 */
public class Type10 extends AbstractTask {

	private static final long serialVersionUID = -6137146531197423207L;

	@Override
	public boolean acceptTask(long teamId, TaskConditionBean taskConditionBean, TaskCondition taskCondition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean executeTask(long teamId, TaskConditionBean taskConditionBean, TaskCondition taskCondition) {
		
		return false;
	}
}
