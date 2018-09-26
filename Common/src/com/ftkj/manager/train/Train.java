package com.ftkj.manager.train;

import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 球队训练馆数据
 * @author qin.jiang
 *
 */
public class Train extends AsynchronousBatchDB {

	private static final long serialVersionUID = 1L;	

	/** 加入锁 */
    private Object lock = new Object();
    
    /** 训练馆唯一ID */
	private int trainId;
	/** 训练馆球队ID */
	private long teamId;
	/** 训练馆等级*/
    private int trainLevel;
    /** 训练馆总经验*/
    private int trainExp;	  
    /** 训练类型 (0:未训练,1:默认4小时,2:8小时) 结算奖励*/
    private int type;
    /** 球员ID*/
    private int playerId;
    /** 球员基础ID(npc)*/
    private int playerRid;
    /** 训练开始时间 */
    private long startTime;
	/** 训练球员攻防 */
	private int playerCap;
    /** 奖励领取状态(0:未领取, 1:可领取, 2:已领取) */
    private int rewardState;
    /** 被抢夺次数 */
    private int robbedNum;
    /** 是否是联盟训练位 (0:个人位,1:联盟位 2:npc位)*/
    private int isLeague;
    /** 训练总时间(联盟训练馆结算) */
    private int trainHour;
    /** 联盟训练馆清理标识(1:清理) */
    private int clear;
    /** 联盟训练馆基础ID */
    private int blId;
    
    public Train() { 
        
    }
    
    public Train(long teamId, int trainId, int isLeague, int blId) {
        super();
        this.trainId = trainId;
        this.teamId = teamId;
        this.trainLevel = 1;
        this.isLeague = isLeague;
        this.blId = blId;
        this.save();
    }
    
    /** 获取,训练馆唯一ID */
    public int getTrainId() {
        return trainId;
    }
    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }
    
    /** 获取,训练馆球队ID */
    public long getTeamId() {
        return teamId;
    }
    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
    public int getTrainLevel() {
        return trainLevel;
    }
    public void setTrainLevel(int trainLevel) {
        this.trainLevel = trainLevel;
    }
    public int getTrainExp() {
        return trainExp;
    }
    public void setTrainExp(int trainExp) {
        this.trainExp = trainExp;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getPlayerId() {
        return playerId;
    }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    public int getPlayerCap() {
        return playerCap;
    }
    public void setPlayerCap(int playerCap) {
        this.playerCap = playerCap;
    } 
    public int getRewardState() {
        return rewardState;
    }
    public void setRewardState(int rewardState) {
        this.rewardState = rewardState;
    }

    public int getIsLeague() {
        return isLeague;
    }
    public void setIsLeague(int isLeague) {
        this.isLeague = isLeague;
    }
    
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPlayerRid() {
        return playerRid;
    }

    public void setPlayerRid(int playerRid) {
        this.playerRid = playerRid;
    }

    public int getRobbedNum() {
        return robbedNum;
    }

    public void setRobbedNum(int robbedNum) {
        this.robbedNum = robbedNum;
    }
    
    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.trainId, this.teamId, this.trainLevel,
                this.trainExp, this.type, this.playerId, this.playerRid, this.startTime, this.playerCap, 
                this.rewardState, this.robbedNum, this.isLeague, this.trainHour, this.clear, this.blId);
    }
    @Override
    public String getRowNames() {
        return "train_id, team_id, train_level, train_exp, type, player_id, player_rid, start_time, player_cap, reward_state, robbed_num, is_league, train_hour, clear, bl_id";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.trainId, this.teamId, this.trainLevel,
            this.trainExp, this.type, this.playerId, this.playerRid, this.startTime, this.playerCap,
            this.rewardState, this.robbedNum, this.isLeague, this.trainHour, this.clear, this.blId);
    }

    @Override
    public String getTableName() {     
        return "t_u_train";
    }
    
    @Override
    public void del() {
        // TODO Auto-generated method stub
        
    }	
    
	@Override
	public String toString() {
		return "Train [lock=" + lock + ", trainId=" + trainId + ", teamId="
				+ teamId + ", trainLevel=" + trainLevel + ", trainExp="
				+ trainExp + ", type=" + type + ", playerId=" + playerId
				+ ", playerRid=" + playerRid + ", startTime=" + startTime
				+ ", playerCap=" + playerCap + ", rewardState=" + rewardState
				+ ", robbedNum=" + robbedNum + ", isLeague=" + isLeague
				+ ", trainHour=" + trainHour + ", clear=" + clear + ", blId="
				+ blId + "]";
	}

	/** 训练结束清理*/
    public void clear() {
        //this.type = 0;
        //this.playerId = 0;
        //this.playerRid = 0;
        this.startTime = 0;
        this.playerCap = 0;
      
        this.save();
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public int getTrainHour() {
        return trainHour;
    }

    public void setTrainHour(int trainHour) {
        this.trainHour = trainHour;
    }

    public int getClear() {
        return clear;
    }

    public void setClear(int clear) {
        this.clear = clear;
    }

    public int getBlId() {
        return blId;
    }

    public void setBlId(int btId) {
        this.blId = btId;
    }


}
