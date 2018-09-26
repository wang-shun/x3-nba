package com.ftkj.manager.train;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 球队训练馆数据
 * @author qin.jiang
 *
 */
public class TrainBean extends ExcelBean{

	/** 训练馆等级*/
    private int level;
    /** 升级需要经验*/
    private int needExp;	
    /** 总经验*/
    private int totalExp; 
    /** 品质 */
    private int quality;
    /** 奖励字串*/
    private List<PropSimple> rewardList;
    
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getNeedExp() {
        return needExp;
    }
    public void setNeedExp(int needExp) {
        this.needExp = needExp;
    }
    public int getTotalExp() {
        return totalExp;
    }
    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }   
    public int getQuality() {
        return quality;
    }
    public void setQuality(int quality) {
        this.quality = quality;
    }
    public List<PropSimple> getRewardList() {
        return rewardList;
    }
    public void setRewardList(List<PropSimple> rewardList) {
        this.rewardList = rewardList;
    }   
    
    @Override
    public void initExec(RowData row) {
        setRewardList(PropSimple.getPropBeanByStringNotConfig(row.get("hourEarn")));        
    }

}
