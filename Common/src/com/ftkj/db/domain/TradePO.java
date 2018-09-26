package com.ftkj.db.domain;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

public class TradePO extends AsynchronousBatchDB {

    private static final long serialVersionUID = 1L;
    private int id;
    /** 球队id */
    private long teamId;
    /** 球员唯一id(动态分配的) */
    private int pid;
    /** 球员基础唯一ID(表配的) */
    private int playerId;
    /** 身价 */
    private int price;
    /** 球员位置 */
    private String position;
    /** 当天市价，以交易结算为准 */
    private int marketPrice;
    /** 球券 */
    private int money;
    /** 0是上架状态； 1自行下架；	2交易完成 */
    private int status = 0;
    /** 創建時間 */
    private DateTime createTime;
    /** 結束時間 */
    private DateTime endTime;
    /** 交易时间 */
    private DateTime dealTime;
    /** 购买者ID */
    private long buyTeam;
    /** 球员天赋数据 */
    private String talent;
    /** 临时数据. 是否置顶. 0 不置顶, 1 置顶 */
    private boolean stickTop;

    /**
     * 创建球员交易
     *
     * @param id
     * @param teamId
     * @param pid
     * @param playerId
     * @param price
     * @param money
     * @param day      上架天数
     */
    public TradePO(int id, long teamId, int pid, int playerId, String position, int price, int money, int day, String talent) {
        super();
        this.id = id;
        this.teamId = teamId;
        this.pid = pid;
        this.playerId = playerId;
        this.position = position;
        this.price = price;
        this.money = money;
        this.talent = talent;
        this.createTime = DateTime.now();
        this.endTime = this.createTime.plusDays(day);
        this.dealTime = GameConsole.Max_Date;
    }

    public TradePO() {
    }

    public String getTalent() {
        return talent;
    }

    public void setTalent(String talent) {
        this.talent = talent;
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

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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

    public int getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public DateTime getDealTime() {
        return dealTime;
    }

    public void setDealTime(DateTime dealTime) {
        this.dealTime = dealTime;
    }

    public long getBuyTeam() {
        return buyTeam;
    }

    public void setBuyTeam(long buyTeam) {
        this.buyTeam = buyTeam;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isStickTop() {
        return stickTop;
    }

    public void setStickTop(boolean stickTop) {
        this.stickTop = stickTop;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.id, this.teamId, this.pid, this.playerId, this.position, this.price, this.marketPrice, this.money, this.talent, this.status, this.createTime, this.endTime, this.dealTime, this.buyTeam);
    }

    @Override
    public String getRowNames() {
        return "id,team_id,pid,player_id,position,price,market_price,money,talent,status,create_time,end_time,deal_time,buy_team";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id, this.teamId, this.pid, this.playerId, this.position, this.price, this.marketPrice, this.money, this.talent, this.status, this.createTime, this.endTime, this.dealTime, this.buyTeam);
    }

    @Override
    public String getTableName() {
        return "t_u_trade";
    }

    @Override
    public void del() {
    }


}
