package com.ftkj.db.domain;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Jay
 * @Description: 待签球员
 * @time:2017年3月28日 下午2:44:15
 */
public class BeSignPlayerPO extends AsynchronousBatchDB {

    private static final long serialVersionUID = 1L;
    /** 自增ID */
    private int id;
    /** 球队ID */
    private long teamId;
    /** 球员ID */
    private int playerId;
    /** 工资帽 */
    private int price;
    /** 天赋ID */
    private int tid;
    /** 是否绑定. true 绑定 */
    private boolean bind;
    /** 过期时间 */
    private DateTime endTime;

    public BeSignPlayerPO() {
        super();
    }

    public BeSignPlayerPO(int id, long teamId, int playerId, int price, int tid, DateTime endTime) {
        super();
        this.id = id;
        this.teamId = teamId;
        this.playerId = playerId;
        this.price = price;
        this.tid = tid;
        this.endTime = endTime;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(id, teamId, playerId, price, tid, bind ? 1 : 0, endTime);
    }

    @Override
    public String getRowNames() {
        return "id, team_id, player_id, price, tid, bind, end_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(id, teamId, playerId, price, tid, bind ? 1 : 0, endTime);
    }

    @Override
    public String getTableName() {
        return "t_u_besign";
    }

    @Override
    public void del() {
        this.endTime = GameConsole.Min_Date;
        this.save();
    }

    @Override
    public synchronized void save() {
        super.save();
    }


}
