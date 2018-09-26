package com.ftkj.manager.league.leagueArean;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;

/**
 * 联盟塞奖励数据
 * @author qin.jiang
 */ 
public class AreanRewardBean extends ExcelBean{
    
    /** 赛区排名*/
    private int rank;
    /** 赛区类型id*/
    private int type;
    /** 奖励的工资帽:有效时间*/
    private Integer[] priceList;
    /** 奖励字串*/
    private String reward;
    /** 联盟贡献*/
    private String honor;
    /** 联盟训练馆ID*/
    private int leaTrainId;
    
    /** 联盟贡献*/
    private String price;
    private PropSimple rewardProp;
    private PropSimple honorProp;

    public PropSimple getRewardProp() {
        return rewardProp;
    }

    public void setRewardProp(PropSimple rewardProp) {
        this.rewardProp = rewardProp;
    }

    public PropSimple getHonorProp() {
        return honorProp;
    }

    public void setHonorProp(PropSimple honorProp) {
        this.honorProp = honorProp;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Integer[] getPriceList() {
        return priceList;
    }

    public void setPriceList(Integer[] priceList) {
        this.priceList = priceList;
    }

    @Override
    public void initExec(RowData row) {
        setPriceList(StringUtil.toIntegerArray(row.get("price"), StringUtil.COLON));
        setRewardProp(PropSimple.getPropSimpleByString(row.get("reward")));
        setHonorProp(PropSimple.getPropSimpleByString(row.get("honor")));
    }

    public String getReward() {
        return reward;
    }

    public String getHonor() {
        return honor;
    }

    public void setHonor(String honor) {
        this.honor = honor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getLeaTrainId() {
        return leaTrainId;
    }

    public void setLeaTrainId(int leaTrainId) {
        this.leaTrainId = leaTrainId;
    }

}
