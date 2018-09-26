package com.ftkj.manager.train;

import java.io.Serializable;

/**
 * 球队训练馆数据
 * @author qin.jiang
 *
 */
public class TrainInfo implements Serializable{

    private static final long serialVersionUID = 1667626480774104860L;
    
    /** 抢夺记录唯一ID */
	private long msgId;
	/** 抢夺球队名称 */
	private String teamName;
	/** 抢夺资源数量*/
    private int num;
    /** 抢夺状态*/
    private int state;	  
    /** 抢夺时间 */
    private long createTime;
    /** 改变标识（1:添加 2:删除） */
    private int flag;
 
    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
