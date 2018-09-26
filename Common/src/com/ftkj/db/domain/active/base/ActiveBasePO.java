package com.ftkj.db.domain.active.base;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDataDB;
import com.ftkj.server.GameSource;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class ActiveBasePO extends AsynchronousBatchDataDB implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 活动ID，数据区分
     */
    private int atvId; // 主键
    private int shardId; // 主键
    private long teamId; // 主键
    private String createDay; // 主键
    //
    private String teamName;
    private int type;
    private DateTime LastTime;
    private DateTime createTime;
    private DateTime updateTime;
    // 常用数据存储项目
    private int iData1;
    private int iData2;
    private int iData3;
    private int iData4;
    private int iData5;
    /** 500长度 */
    private String sData1 = "";
    /** 500长度 */
    private String sData2 = "";
    /** 500长度 */
    private String sData3 = "";
    /** 500长度 */
    private String sData4 = "";
    /** 500长度 */
    private String sData5 = "";
    /** 500长度 */
    private String sPropNum = "";
    /** 500长度 */
    private String sFinishStatus = "";
    /** 500长度 */
    private String sAwardStatus = "";

    public ActiveBasePO() {
        this.createTime = DateTime.now();
        this.createDay = DateTimeUtil.getString(this.createTime);
        this.updateTime = GameConsole.Min_Timestamp;
        this.LastTime = GameConsole.Min_Timestamp;
    }

    /**
     * 通用构造
     *
     * @param atvId
     * @param shardId
     * @param teamId
     * @param teamName
     */
    public ActiveBasePO(int atvId, int shardId, long teamId, String teamName) {
        super();
        this.atvId = atvId;
        this.shardId = shardId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.createTime = DateTime.now();
        this.createDay = DateTimeUtil.getString(this.createTime);
        this.updateTime = GameConsole.Min_Timestamp;
        this.LastTime = GameConsole.Min_Timestamp;
    }

    public int getAtvId() {
        return atvId;
    }

    public void setAtvId(int atvId) {
        this.atvId = atvId;
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DateTime getLastTime() {
        return LastTime;
    }

    public void setLastTime(DateTime lastTime) {
        LastTime = lastTime;
    }

    public String getCreateDay() {
        return createDay;
    }

    public void setCreateDay(String createDay) {
        this.createDay = createDay;
    }

    public int getiData4() {
        return iData4;
    }

    public void setiData4(int iData4) {
        this.iData4 = iData4;
    }

    public int getiData5() {
        return iData5;
    }

    public void setiData5(int iData5) {
        this.iData5 = iData5;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public int getiData1() {
        return iData1;
    }

    public void setiData1(int iData1) {
        this.iData1 = iData1;
    }

    public int getiData2() {
        return iData2;
    }

    public void setiData2(int iData2) {
        this.iData2 = iData2;
    }

    public int getiData3() {
        return iData3;
    }

    public void setiData3(int iData3) {
        this.iData3 = iData3;
    }

    public String getsData1() {
        return sData1;
    }

    public void setsData1(String sData1) {
        this.sData1 = sData1;
    }

    public String getsData2() {
        return sData2;
    }

    public void setsData2(String sData2) {
        this.sData2 = sData2;
    }

    public String getsData3() {
        return sData3;
    }

    public void setsData3(String sData3) {
        this.sData3 = sData3;
    }

    public String getsData4() {
        return sData4;
    }

    public void setsData4(String sData4) {
        this.sData4 = sData4;
    }

    public String getsData5() {
        return sData5;
    }

    public void setsData5(String sData5) {
        this.sData5 = sData5;
    }

    public String getFinishStatus() {
        return sFinishStatus;
    }

    public void setFinishStatus(String finishStatus) {
        this.sFinishStatus = finishStatus;
    }

    public String getAwardStatus() {
        return sAwardStatus;
    }

    public void setAwardStatus(String awardStatus) {
        this.sAwardStatus = awardStatus;
    }

    public String getsPropNum() {
        return sPropNum;
    }

    public void setsPropNum(String sPropNum) {
        this.sPropNum = sPropNum;
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.atvId, this.shardId, this.teamId, this.teamName, this.type, this.LastTime, this.createDay, this.createTime, this.updateTime, this.iData1, this.iData2, this.iData3, this.iData4, this.iData5, this.sData1, this.sData2, this.sData3, this.sData4, this.sData5, this.sPropNum, this.sFinishStatus, this.sAwardStatus);
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.atvId, this.shardId, this.teamId, this.teamName, this.type, this.LastTime, this.createDay, this.createTime, this.updateTime, this.iData1, this.iData2, this.iData3, this.iData4, this.iData5, this.sData1, this.sData2, this.sData3, this.sData4, this.sData5, this.sPropNum, this.sFinishStatus, this.sAwardStatus);
    }

    @Override
    public String getRowNames() {
        return "atv_id, shard_id, team_id, team_name, type, last_time, create_day, create_time, update_time, i_data1, i_data2, i_data3, i_data4, i_data5, s_data1, s_data2, s_data3, s_data4, s_data5, prop_num, finish_status, award_status";
    }

    /**
     * 每区一个表
     *
     * @return
     */
    @Override
    public String getTableName() {
        return "t_u_active_data_" + GameSource.shardId;
    }

    /**
     * 每区一个表
     *
     * @return
     */
    public static String getTableNameDB() {
        return "t_u_active_data_" + GameSource.shardId;
    }

    @Override
    public void del() {
        // atv_id = 0, team_id=0, 则删除

    }

}
