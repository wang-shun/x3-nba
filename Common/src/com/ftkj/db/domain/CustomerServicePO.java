package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;

import java.util.List;

/**
 * 客服工单.
 * @author mr.lei
 * @time 2018-8-31 12:02:09
 */
public class CustomerServicePO extends AsynchronousBatchDB {

    private static final long serialVersionUID = 1L;

    /** id */
    private long csId;
    /**区服名称*/
    private String areaName;
    /** 球队Id */
    private long teamId;
    /**VIP等级*/
    private int vipLevel;
    /**玩家名称*/
    private String playerName;
    /**手机号码*/
    private String telphone;
    /**玩家QQ号*/
    private String qq;
    /**问题描述*/
    private String problem;
    /**客服回答*/
    private String response;
    /**提问回复状态:0未回复,1已回复未读,2已回复已读*/
    private String respStatus = "0";
    /**问题发生的时间*/
    private String occurTime;
    /**是否删除:0未删除,1已删除*/
    private int deleteFlag = 0;
    /** 日期 */
    private DateTime createTime;

    public CustomerServicePO() {
    }
    
    
    public CustomerServicePO(long csId, String areaName, long teamId,
			int vipLevel, String playerName, String telphone, String qq, String problem,
			String response, String respStatus, String occurTime) {
		super();
		this.csId = csId;
		this.areaName = areaName;
		this.teamId = teamId;
		this.vipLevel = vipLevel;
		this.playerName = playerName;
		this.telphone = telphone;
		this.qq = qq;
		this.problem = problem;
		this.response = response;
		this.respStatus = respStatus;
		this.occurTime = occurTime;
		this.createTime = DateTime.now();
	}

	public long getCsId() {
		return csId;
	}


	public void setCsId(long csId) {
		this.csId = csId;
	}


	public String getAreaName() {
		return areaName;
	}


	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}


	public long getTeamId() {
		return teamId;
	}


	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}


	public int getVipLevel() {
		return vipLevel;
	}


	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}


	public String getPlayerName() {
		return playerName;
	}


	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}


	public String getTelphone() {
		return telphone;
	}


	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getQq() {
		return qq;
	}


	public void setQq(String qq) {
		this.qq = qq;
	}


	public String getProblem() {
		return problem;
	}


	public void setProblem(String problem) {
		this.problem = problem;
	}


	public String getResponse() {
		return response;
	}


	public void setResponse(String response) {
		this.response = response;
	}

	/**提问回复状态:0未回复,1已回复未读,2已回复已读*/
	public String getRespStatus() {
		return respStatus;
	}


	public void setRespStatus(String respStatus) {
		this.respStatus = respStatus;
	}


	public String getOccurTime() {
		return occurTime;
	}


	public void setOccurTime(String occurTime) {
		this.occurTime = occurTime;
	}
	
	public int getDeleteFlag() {
		return deleteFlag;
	}


	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}


	public DateTime getCreateTime() {
		return createTime;
	}


	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}


	@Override
    public String getSource() {
        return StringUtil.formatSQL(this.csId,
        this.areaName,
        this.teamId,
		this.vipLevel,
		this.playerName,
		this.telphone,
		this.problem,
		this.response,
		this.respStatus,
		this.occurTime,
		this.deleteFlag,
		this.createTime);
    }

    @Override
    public String getRowNames() {
        return "cs_id,area_name,team_id,vip_level,player_name,telphone,"
        		+ "problem,response,resp_status,occur_time,delete_flag,create_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.csId,
                this.areaName,
                this.teamId,
        		this.vipLevel,
        		this.playerName,
        		this.telphone,
        		this.problem,
        		this.response,
        		this.respStatus,
        		this.occurTime,
        		this.deleteFlag,
        		this.createTime);
    }

    @Override
    public synchronized void save() {
        super.save();
    }

    @Override
    public String getTableName() {
        return "t_u_customer_service";
    }

    @Override
    public void del() {
    	this.deleteFlag = 1;
    	save();
    }

}
