package com.ftkj.manager.custom;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.ECustomGuessType;
import com.ftkj.enums.ECustomPVPType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.team.TeamNodeInfo;
import com.ftkj.util.JsonUtil;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年8月1日
 * 自定义比赛房间
 */
public class CustomPVPRoom extends AsynchronousBatchDB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //房间ID
    private int roomId;
    //比赛类型
    private ECustomPVPType pkType;
    //竞猜类型
    private ECustomGuessType guessType;
    //获胜条件
    private EActionType winCondition;
    //战力条件
    private int powerCondition;
    //等级条件
    private int levelCondition;

    //结算小节集合
    private Set<EBattleStep> stepConditions;
    //结算球员位置
    private EPlayerPosition positionCondition;
    //房间状态
    private EBattleRoomStatus roomStatus;
    //房间宣言
    private String roomTip;

    //房间筹码
    private int roomMoney;
    //房间让分
    private int roomScore;

    //竞猜双方投注额
    private int homeMoney;
    private int awayMoney;

    //房主信息
    private TeamNodeInfo homeTeam;
    //挑战者信息
    private TeamNodeInfo awayTeam;

    private boolean autoStart;

    private DateTime startTime;

    //-------------------------------------------------

    //参与投注的节点
    private Set<String> nodes;

    private long battleId;
    private String battleClientNode;

    public CustomPVPRoom() {
    }

    public CustomPVPRoom(int roomId) {
        this.roomId = roomId;
    }

    public CustomPVPRoom createCustomPVPRoom(TeamNodeInfo team, int winType, String stepCondition, int positionCondition, int pkType
            , int powerCondition, int levelCondition, int roomScore, int roomMoney, String roomTip, boolean autoStart) {
        this.setAutoStart(autoStart);
        this.setAwayMoney(0);
        this.setGuessType(ECustomGuessType.values()[RandomUtil.randInt(ECustomGuessType.values().length)]);
        this.setHomeMoney(0);
        this.setHomeTeam(team);
        this.setLevelCondition(levelCondition);
        Set<String> nodes = Sets.newHashSet();
        nodes.add(team.getNodeName());
        this.setNodes(nodes);
        this.setPkType(ECustomPVPType.values()[pkType]);
        this.setPositionCondition(EPlayerPosition.getEPlayerPosition(positionCondition));
        this.setPowerCondition(powerCondition);
        this.setRoomId(roomId);
        this.setRoomMoney(roomMoney);
        this.setRoomScore(roomScore);
        this.setRoomTip(roomTip);
        this.setRoomStatus(EBattleRoomStatus.默认未匹配);
        this.startTime = DateTime.now();
        Set<EBattleStep> steps = Arrays.stream(StringUtil.toIntArray(stepCondition, StringUtil.DEFAULT_ST))
                .mapToObj(step -> EBattleStep.values()[step]).collect(Collectors.toSet());
        this.setStepConditions(steps);
        this.setWinCondition(EActionType.convertByType(winType));
        this.save();
        return this;
    }

    /**
     * 有玩家进入，准备开启
     */
    public void ready(TeamNodeInfo other) {
        this.awayTeam = other;
        if (this.autoStart)//开启自动开启
        { this.startTime = DateTime.now().plusSeconds(5); }
        save();
    }

    public boolean isStart(DateTime now) {
        return this.startTime != null && this.startTime.isBefore(now.getMillis());
    }

    public boolean autoClose(DateTime end) {
        return this.startTime != null && this.startTime.isBefore(end.getMillis());
    }

    public void updateHomeInfo(TeamNodeInfo info) {
        this.homeTeam = info;
    }

    public void addNode(String node) {
        this.nodes.add(node);
        this.save();
    }

    /**
     * 对手玩家退出
     */
    public void quitOhter() {
        this.awayTeam = null;
        this.roomStatus = EBattleRoomStatus.默认未匹配;
    }

    /**
     * 房主退出
     */
    public int close() {
        this.startTime = null;
        this.homeTeam = null;
        this.awayTeam = null;
        return this.roomId;
    }

    public void updateHomeMoney(int val) {
        this.homeMoney += val;
        this.save();
    }

    public void updateAwayMoney(int val) {
        this.awayMoney += val;
        this.save();
    }

    static final float limit = 0.0618f;
    static final float rate = -2.5f;

    public float getHomeMoneyRate() {
        float pot = this.homeMoney + this.awayMoney;
        if (pot == 0) { return 1; }
        float money = this.homeMoney <= 0 ? 1 : this.homeMoney;
        double commission = limit / (1 + Math.exp(-(pot / money) + (rate - 1)));
        double odds = ((pot - money) * (1 - commission) + money) / money;
        return (float) odds;
    }

    public float getAwayMoneyRate() {
        float pot = this.homeMoney + this.awayMoney;
        if (pot == 0) { return 1; }
        float money = this.awayMoney <= 0 ? 1 : this.awayMoney;
        double commission = limit / (1 + Math.exp(-(pot / money) + (rate - 1)));
        double odds = ((pot - money) * (1 - commission) + money) / money;
        return (float) odds;
    }

    public long getRoomTeamId() {
        return this.homeTeam == null ? 0 : this.homeTeam.getTeamId();
    }

    public boolean hasTeam(long teamId) {
        return (this.homeTeam == null ? 0 : this.homeTeam.getTeamId()) == teamId ||
                (this.awayTeam == null ? 0 : this.awayTeam.getTeamId()) == teamId;
    }

    public boolean checkPowerLimit(int power) {
        int limit = Math.round(this.homeTeam.getPower() * this.powerCondition / 100f);
        return power < (this.homeTeam.getPower() - limit) || power > (this.homeTeam.getPower() + limit);
    }

    public boolean checkLevelLimit(int level) {
        return level < (this.homeTeam.getLevel() - this.levelCondition) || level > (this.homeTeam.getLevel() + this.levelCondition);
    }

    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
    }

    public String getBattleClientNode() {
        return battleClientNode;
    }

    public void setBattleClientNode(String battleClientNode) {
        this.battleClientNode = battleClientNode;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public int getRoomId() {
        return roomId;
    }

    public ECustomPVPType getPkType() {
        return pkType;
    }

    public ECustomGuessType getGuessType() {
        return guessType;
    }

    public EActionType getWinCondition() {
        return winCondition;
    }

    public int getPowerCondition() {
        return powerCondition;
    }

    public int getLevelCondition() {
        return levelCondition;
    }

    public Set<EBattleStep> getStepConditions() {
        return stepConditions;
    }

    public EPlayerPosition getPositionCondition() {
        return positionCondition;
    }

    public EBattleRoomStatus getRoomStatus() {
        return roomStatus;
    }

    public String getRoomTip() {
        return roomTip;
    }

    public int getRoomMoney() {
        return roomMoney;
    }

    public int getRoomScore() {
        return roomScore;
    }

    public int getHomeMoney() {
        return homeMoney;
    }

    public int getAwayMoney() {
        return awayMoney;
    }

    public TeamNodeInfo getHomeTeam() {
        return homeTeam;
    }

    public TeamNodeInfo getAwayTeam() {
        return awayTeam;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setPkType(ECustomPVPType pkType) {
        this.pkType = pkType;
    }

    public void setGuessType(ECustomGuessType guessType) {
        this.guessType = guessType;
    }

    public void setWinCondition(EActionType winCondition) {
        this.winCondition = winCondition;
    }

    public void setPowerCondition(int powerCondition) {
        this.powerCondition = powerCondition;
    }

    public void setLevelCondition(int levelCondition) {
        this.levelCondition = levelCondition;
    }

    public void setStepConditions(Set<EBattleStep> stepConditions) {
        this.stepConditions = stepConditions;
    }

    public void setPositionCondition(EPlayerPosition positionCondition) {
        this.positionCondition = positionCondition;
    }

    public void setRoomStatus(EBattleRoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void setRoomTip(String roomTip) {
        this.roomTip = roomTip;
    }

    public void setRoomMoney(int roomMoney) {
        this.roomMoney = roomMoney;
    }

    public void setRoomScore(int roomScore) {
        this.roomScore = roomScore;
    }

    public void setHomeMoney(int homeMoney) {
        this.homeMoney = homeMoney;
    }

    public void setAwayMoney(int awayMoney) {
        this.awayMoney = awayMoney;
    }

    public void setHomeTeam(TeamNodeInfo homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(TeamNodeInfo awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.roomId, this.pkType.ordinal(), this.guessType.ordinal()
                , this.winCondition.getType(), this.powerCondition, this.levelCondition, this.stepConditions.stream().map(step -> "" + step.ordinal()).collect(Collectors.joining(","))
                , this.positionCondition.getId(), this.roomStatus.ordinal(), this.roomTip, this.roomMoney, this.roomScore
                , this.homeMoney, this.awayMoney, this.homeTeam == null ? "" : JsonUtil.toJson(this.homeTeam)
                , this.awayTeam == null ? "" : JsonUtil.toJson(this.awayTeam), this.nodes.stream().collect(Collectors.joining(","))
                , this.startTime);
    }

    @Override
    public String getRowNames() {
        return "room_id,pk_type,guess_type,win_condition,power_condition,level_condition,step_condition,position_condition,room_status,room_tip,room_money,room_score,home_money,away_money,home_team,away_team,nodes,start_time";
    }


    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.roomId, this.pkType.ordinal(), this.guessType.ordinal()
            , this.winCondition.getType(), this.powerCondition, this.levelCondition, this.stepConditions.stream().map(step -> "" + step.ordinal()).collect(Collectors.joining(","))
            , this.positionCondition.getId(), this.roomStatus.ordinal(), this.roomTip, this.roomMoney, this.roomScore
            , this.homeMoney, this.awayMoney, this.homeTeam == null ? "" : JsonUtil.toJson(this.homeTeam)
            , this.awayTeam == null ? "" : JsonUtil.toJson(this.awayTeam), this.nodes.stream().collect(Collectors.joining(","))
            , this.startTime);
    }

    @Override
    public String getTableName() {
        return "t_u_custom_room";
    }

    @Override
    public void del() {

    }

}
