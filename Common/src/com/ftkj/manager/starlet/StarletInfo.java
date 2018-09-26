package com.ftkj.manager.starlet;

import java.io.Serializable;

/**
 * 球队新秀挑战数据
 * @author qin.jiang
 *
 */
public class StarletInfo implements Serializable{

    private static final long serialVersionUID = 1667626480774104860L;
    
    /** 唯一ID */
	private long msgId;
	/** 挑战状态(0:挑战,1:被挑战) */
    private int cState;
	/** 目标球队名称 */
	private String teamName;
	/** 下降或上升排名*/
    private int num;
    /** 挑战状态(0:失败, 1:成功)*/
    private int state;	  
    /** 挑战时间 */
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

    public int getcState() {
        return cState;
    }

    public void setcState(int cState) {
        this.cState = cState;
    }  
}
