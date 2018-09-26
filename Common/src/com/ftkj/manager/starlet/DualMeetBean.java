package com.ftkj.manager.starlet;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;

/**
 * 新秀对抗赛类型基础数据
 * @author qin.jiang
 */
public class DualMeetBean extends ExcelBean{
    
    /** 比赛类型*/
    private int type;   
    /** 初始NPC球队*/
    private int initNpcTeam;
    /** 奖励字串*/
    private List<PropSimple> rewardList;   
    /** 对抗赛获胜基数*/
    private String radix;
    private Integer[] radixList;
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getInitNpcTeam() {
        return initNpcTeam;
    }

    public void setInitNpcTeam(int initNpcTeam) {
        this.initNpcTeam = initNpcTeam;
    }

    public List<PropSimple> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<PropSimple> rewardList) {
        this.rewardList = rewardList;
    }
    
    public String getRadix() {
        return radix;
    }

    public void setRadix(String radix) {
        this.radix = radix;
    }

    public Integer[] getRadixList() {
        return radixList;
    }

    public void setRadixList(Integer[] radixList) {
        this.radixList = radixList;
    }
    
    @Override
    public void initExec(RowData row) {
        setRewardList(PropSimple.getPropBeanByStringNotConfig(row.get("reward"))); 
        setRadixList(StringUtil.toIntegerArray(row.get("radix"), StringUtil.COLON));
    }
}
