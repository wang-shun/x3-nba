package com.ftkj.db.domain;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年3月3日
 * 单个球员数据
 */
public class PlayerPO extends AsynchronousBatchDB {
    private long teamId;
    /** 球员唯一id */
    private int id;
    /** 天赋id */
    private int talentId;
    /** 策划配置id */
    private int playerRid;
    /** 工资帽 */
    private int price;
    /** 位置 */
    private String position;
    /** 阵容位置 */
    private String lineupPosition;
    /** 创建时间 */
    private DateTime createTime;
    /**
     * 是否在仓库，0在阵容中， 1在仓库中 2在交易市场中  3已删除 4训练馆中
     */
    private int storage;
    /** 是否绑定. true 绑定 */
    private boolean bind;

    public PlayerPO() {
    }

    public int getTalentId() {
        return talentId;
    }

    public void setTalentId(int talentId) {
        this.talentId = talentId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerRid(int playerRid) {
        this.playerRid = playerRid;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setLineupPosition(String lineupPosition) {
        this.lineupPosition = lineupPosition;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public int getPrice() {
        return price;
    }

    public String getPosition() {
        return position;
    }

    public String getLineupPosition() {
        return lineupPosition;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public long getTeamId() {
        return teamId;
    }

    public int getId() {
        return id;
    }

    public int getPlayerRid() {
        return playerRid;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(teamId, id, playerRid, talentId, price, position,
            lineupPosition, storage, bind ? 1 : 0, createTime);
    }

    @Override
    public String getRowNames() {
        return "team_id, pid, player_id, tid, price, `position`, lineup_position, storage, bind, create_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(teamId, id, playerRid, talentId, price, position,
            lineupPosition, storage, bind ? 1 : 0, createTime);
    }

    @Override
    public String getTableName() {
        return "t_u_player";
    }

    @Override
    public void del() {
        this.storage = 3;
        save();
    }
}
