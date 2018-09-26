package com.ftkj.manager.npc;

import com.ftkj.db.domain.bean.NPCBeanVO;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * npc
 *
 * @author tim.huang
 */
public class NPCBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(NPCBean.class);
    private static final long serialVersionUID = 1L;
    /**
     * 战术等级
     */
    private long npcId;
    private String npcName;
    private int level;
    private String logo;
    private int attack;
    private int defend;
    /** 默认进攻战术(不配置则使用全局配置) */
    private int tacticOffense;
    /** 默认防守战术 */
    private int tacticDefense;
    private int useTactics;
    private int bestTactics;
    private int tacticsLevel;
    private int useProp;
    private int round;
    private int totalProp;
    private int playerAttack;
    private int playerDefend;
    private int attackCap;
    private int defendCap;
    private int capBuf;
    private String players;
    /** ai规则分组id */
    private int aiGroupId;
    private Map<Integer, Integer> propAddMap;

    public NPCBean(NPCBeanVO po) {
        this(po, po.getNpcId(), po.getNpcName());
    }

    public NPCBean(NPCBeanVO po, long npcId, String name) {
        this.npcId = npcId;
        this.npcName = name;
        this.level = po.getLevel();
        this.logo = po.getLogo();
        this.defend = po.getDefend();
        this.attack = po.getAttack();
        this.useTactics = po.getUseTactics();
        this.bestTactics = po.getBestTactics();
        this.useProp = po.getUseProp();
        this.round = po.getRound();
        this.propAddMap = Maps.newHashMap();
        this.playerAttack = po.getPlayerAttack();
        this.playerDefend = po.getPlayerDefend();
        this.tacticsLevel = po.getTacticsLevel();
        this.tacticOffense = po.getTacticOffense();
        this.tacticDefense = po.getTacticDefense();

        this.attackCap = po.getAttack();
        this.defendCap = po.getDefend();
        this.capBuf = po.getCapBuf();
        this.players = po.getPlayers();
        this.aiGroupId = po.getAiGroupId();
        String[] props = StringUtil.toStringArray(po.getProps(), StringUtil.DEFAULT_ST);
        for (String p : props) {
            int[] v = StringUtil.toIntArray(p, StringUtil.DEFAULT_FH);
            this.propAddMap.put(v[0], v[1]);
            this.totalProp += v[1];
        }
    }

    public int getLevel() {
        return level;
    }

    public String getLogo() {
        return logo;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefend() {
        return defend;
    }

    public int getUseTactics() {
        return useTactics;
    }

    public int getBestTactics() {
        return bestTactics;
    }

    public int getUseProp() {
        return useProp;
    }

    public int getRound() {
        return round;
    }

    public int getTotalProp() {
        return totalProp;
    }

    public int getCapBuf() {
        return capBuf;
    }

    public int getPlayerAttack() {
        return playerAttack;
    }

    public int getPlayerDefend() {
        return playerDefend;
    }

    public int getAttackCap() {
        return attackCap;
    }

    public int getDefendCap() {
        return defendCap;
    }

    public int getTacticsLevel() {
        return tacticsLevel;
    }

    public int getTacticOffense() {
        return tacticOffense;
    }

    public int getTacticDefense() {
        return tacticDefense;
    }

    public int getAiGroupId() {
        return aiGroupId;
    }

    public String getPlayers() {
        return players;
    }

    public long getNpcId() {
        return npcId;
    }

    public List<PropSimple> getPropList() {
        return propAddMap.keySet()
                .stream()
                .map(pid -> new PropSimple(pid, 999))
                .collect(Collectors.toList());
    }

    public String getNpcName() {
        return npcName;
    }

    @Override
    public String toString() {
        return "{" +
                "\"npcId\":" + npcId +
                ", \"npcName\":\"" + npcName + "\"" +
                ", \"level\":" + level +
                ", \"logo\":\"" + logo + "\"" +
                ", \"attack\":" + attack +
                ", \"defend\":" + defend +
                ", \"tacticOffense\":" + tacticOffense +
                ", \"tacticDefense\":" + tacticDefense +
                ", \"useTactics\":" + useTactics +
                ", \"bestTactics\":" + bestTactics +
                ", \"tacticsLevel\":" + tacticsLevel +
                ", \"useProp\":" + useProp +
                ", \"round\":" + round +
                ", \"totalProp\":" + totalProp +
                ", \"playerAttack\":" + playerAttack +
                ", \"playerDefend\":" + playerDefend +
                ", \"attackCap\":" + attackCap +
                ", \"defendCap\":" + defendCap +
                ", \"capBuf\":" + capBuf +
                ", \"players\":\"" + players + "\"" +
                ", \"aiGroupId\":" + aiGroupId +
                '}';
    }
}
