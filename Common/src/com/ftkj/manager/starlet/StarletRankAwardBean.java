package com.ftkj.manager.starlet;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 新秀排位赛奖励基础数据
 * @author qin.jiang
 */
public class StarletRankAwardBean extends ExcelBean{
    
    /** 排名 */
    private int rank;
    
    private String reward;
    /** 奖励字串*/
    private List<PropSimple> rewardList;   
    
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<PropSimple> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<PropSimple> rewardList) {
        this.rewardList = rewardList;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }
    
    @Override
    public void initExec(RowData row) {
        setRewardList(PropSimple.getPropBeanByStringNotConfig(row.get("reward")));    
    }
}
