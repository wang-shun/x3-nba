package com.ftkj.db.domain.bean;

/**
 * @author tim.huang
 * 2017年3月27日
 */
public class NPCBeanVO {
    private long npcId;
    private String npcName;
    private int level;
    private String logo;
    private int attack;
    private int defend;
    private int playerAttack;
    private int playerDefend;
    private int tacticsLevel;
    /** 默认进攻战术(不配置则使用全局配置) */
    private int tacticOffense;
    /** 默认防守战术 */
    private int tacticDefense;
    private int useTactics;
    private int bestTactics;
    private int useProp;
    private String props;
    private int round;
    private String players;
    private int capBuf;
    /** ai规则分组id */
    private int aiGroupId;

    public int getCapBuf() {
        return capBuf;
    }

    public void setCapBuf(int capBuf) {
        this.capBuf = capBuf;
    }

    public long getNpcId() {
        return npcId;
    }

    public void setNpcId(long npcId) {
        this.npcId = npcId;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefend() {
        return defend;
    }

    public void setDefend(int defend) {
        this.defend = defend;
    }

    public int getPlayerAttack() {
        return playerAttack;
    }

    public void setPlayerAttack(int playerAttack) {
        this.playerAttack = playerAttack;
    }

    public int getPlayerDefend() {
        return playerDefend;
    }

    public void setPlayerDefend(int playerDefend) {
        this.playerDefend = playerDefend;
    }

    public int getTacticsLevel() {
        return tacticsLevel;
    }

    public void setTacticsLevel(int tacticsLevel) {
        this.tacticsLevel = tacticsLevel;
    }

    public int getTacticOffense() {
        return tacticOffense;
    }

    public int getTacticDefense() {
        return tacticDefense;
    }

    public int getUseTactics() {
        return useTactics;
    }

    public void setUseTactics(int useTactics) {
        this.useTactics = useTactics;
    }

    public int getBestTactics() {
        return bestTactics;
    }

    public void setBestTactics(int bestTactics) {
        this.bestTactics = bestTactics;
    }

    public int getUseProp() {
        return useProp;
    }

    public void setUseProp(int useProp) {
        this.useProp = useProp;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public int getAiGroupId() {
        return aiGroupId;
    }
}
