package com.ftkj.manager.task;

import com.ftkj.console.TaskConsole;
import com.ftkj.db.domain.bean.TaskBeanVO;

import java.util.List;

/**
 * @author tim.huang
 * 2017年6月7日
 * 日常任务
 */
public class TaskDayBean extends TaskBean {

    private int dayType;
    private int star;

    public TaskDayBean(TaskBeanVO vo, List<TaskConditionBean> conditionList) {
        super(vo, conditionList);
        this.dayType = vo.getDayType();
        this.star = vo.getStar();
    }

    @Override
    public int getDayType() {
        return dayType;
    }

    public int getStar() {
        return star;
    }

    private StarBean getStarBean(int star) {
        return TaskConsole.getStarMap().get(star);
    }

    public float getExp(int level) {
        StarBean sb = getStarBean(star);
        return level * sb.getNum() * sb.getRatio();
    }
}