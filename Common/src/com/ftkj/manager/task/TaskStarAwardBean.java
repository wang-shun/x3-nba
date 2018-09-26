package com.ftkj.manager.task;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 日常任务星星奖励配置
 * @author qin.jiang
 */ 
public class TaskStarAwardBean extends ExcelBean{
	
	/** 奖励ID*/
	private int id;
	/** 任务星数*/
	private int starNum;
	/** 奖励字串*/
	private List<PropSimple> rewardList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStarNum() {
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
    }

    public List<PropSimple> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<PropSimple> rewardList) {
        this.rewardList = rewardList;
    }

    @Override
    public void initExec(RowData row) {
        rewardList = PropSimple.getPropBeanByStringNotConfig(row.get("reward"));        
    }	
}
