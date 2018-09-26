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
public class LeagueTrainBean extends ExcelBean{

	/** 联盟训练馆ID */
    private int leaTrainId;
    /** 提高攻防比(百分比)*/
    private int cap;   
    /** 训练馆产出(hour)*/
    private String leaHourEarn;
    private List<PropSimple> rewardList;

    @Override
    public void initExec(RowData row) {
        setRewardList(PropSimple.getPropBeanByStringNotConfig(row.get("leaHourEarn")));        
    }

    public int getLeaTrainId() {
        return leaTrainId;
    }

    public void setLeaTrainId(int leaTrainId) {
        this.leaTrainId = leaTrainId;
    }


    public int getCap() {
        return cap;
    }


    public void setCap(int cap) {
        this.cap = cap;
    }


    public String getLeaHourEarn() {
        return leaHourEarn;
    }


    public void setLeaHourEarn(String leaHourEarn) {
        this.leaHourEarn = leaHourEarn;
    }


    public List<PropSimple> getRewardList() {
        return rewardList;
    }


    public void setRewardList(List<PropSimple> rewardList) {
        this.rewardList = rewardList;
    }

}
