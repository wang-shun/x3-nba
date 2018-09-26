package com.ftkj.manager.train;

/**
 * 球队训练馆数据
 * @author qin.jiang
 *
 */
public class TrainTypeBean{

	/** 训练类型*/
    private int type;
    /** 训练时长（分*/
    private int time;
    
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }	  
}
