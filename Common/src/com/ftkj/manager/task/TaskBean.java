package com.ftkj.manager.task;

import java.util.List;
import java.util.Map;

import com.ftkj.db.domain.bean.TaskBeanVO;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ETaskType;
import com.ftkj.manager.prop.PropSimple;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年6月7日
 * 主线任务
 */
public class TaskBean {
	
	/** 任务ID*/
	private int tid;
	/** 任务类型*/
	private ETaskType type;
	/** 任务名称*/
	private String name;
	/** 奖励掉落ID*/
	private List<PropSimple> props;
	/** 任务开启等级*/
	private int limitLevel;
	/** 任务显示等级*/
	private int showLevel;
	/** 是否显示目标任务 */
	private int teamPurpose;
	/** 前置任务*/
	private int limitTid;	
	/** 完成任务状态，控制显示(1:显示)*/
    private int dayType;
	
	/** 任务条件列表*/
	private Map<ETaskCondition,TaskConditionBean> conditions;
	
	public TaskBean(TaskBeanVO vo,List<TaskConditionBean> conditionList){
		this.tid = vo.getTid();
		this.type = ETaskType.getETaskType(vo.getType());
		this.name = vo.getName();
		this.props = PropSimple.getPropBeanByStringNotConfig(vo.getProps());
		this.limitLevel = vo.getLimitLevel();
		this.showLevel = vo.getShowLevel();		
		this.teamPurpose = vo.getTeamPurpose();
		this.conditions = Maps.newHashMap();
		this.limitTid = vo.getLast_task();
		this.dayType = vo.getDayType();
		if(conditionList!=null && conditionList.size()>0) {
			conditionList.forEach(con->this.conditions.put(con.getCondition(), con));
		}
	}
	
	public int getLimitTid() {
		return limitTid;
	}

	public int getShowLevel() {
		return showLevel;
	}
	public int getTid() {
		return tid;
	}
	public ETaskType getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public List<PropSimple> getProps() {
		return props;
	}
	public int getLimitLevel() {
		return limitLevel;
	}
	public Map<ETaskCondition, TaskConditionBean> getConditions() {
		return conditions;
	}
	public int getTeamPurpose() {
		return teamPurpose;
	}
	public void setTeamPurpose(int teamPurpose) {
		this.teamPurpose = teamPurpose;
	}

	@Override
	public String toString() {
		return "TaskBean [tid=" + tid + ", type=" + type + ", name=" + name + ", conditions=" + conditions + "]";
	}

    public int getDayType() {
        return dayType;
    }

    public void setDayType(int dayType) {
        this.dayType = dayType;
    }	
}
