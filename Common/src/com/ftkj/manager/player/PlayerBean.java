package com.ftkj.manager.player;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ENBAPlayerTeam;
import com.ftkj.enums.ENBAPlayerTitle;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年2月16日
 * 球员配置数据
 */
public class PlayerBean implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 球员id */
    private int resourceId;
    /** 所属球队 */
    private ENBAPlayerTeam team;
    /** 球员名字 */
    private String name;
    /** 简称 */
    private String shortName;
    /** 英文名 */
    private String enName;
    /** 球员等级 */
    private EPlayerGrade grade;
    /** 工资帽 */
    private int price;
    /** 前一天的工资 */
    private int beforePrice;
    /** 前一天的总能力 */
    private int beforeCap;
    /** 称号 */
    private ENBAPlayerTitle playerTitle;
    /** 伤病 */
    private boolean injured;
    /** 伤病位置 */
    private EPlayerPosition[] position;
    /* 新秀年份_轮数_顺位*/
    private int[] draft;

    private Map<EActionType, Float> ability;
    private Map<EActionType, Float> avgAbility;

    public PlayerBean(PlayerBeanVO po, PlayerBeanVO avg, int powerRate) {
        this.resourceId = po.getPlayerId();
        this.team = ENBAPlayerTeam.getENBAPlayerTeam(po.getTeamId());
        this.name = po.getName();
        this.shortName = po.getShortName();
        this.grade = EPlayerGrade.convertByName(po.getGrade());
        this.price = po.getPrice();
        this.beforePrice = po.getBeforePrice();
        this.playerTitle = ENBAPlayerTitle.getEPlayerTitle(po.getPlayerType());
        this.injured = po.getInjured() == 1;
        this.position = new EPlayerPosition[2];
        this.beforeCap = po.getBeforeCap();
        this.enName = po.getEnName();
        initPosition(po.getPosition());
        this.ability = Maps.newHashMap();
        this.avgAbility = Maps.newHashMap();

        this.ability.put(EActionType.fgm, po.getFgm());
        this.ability.put(EActionType.ftm, po.getFtm());
        this.ability.put(EActionType.pts, po.getPts());
        this.ability.put(EActionType._3pm, po.getThreePm());
        this.ability.put(EActionType._3pa, po.getThreePa());
        this.ability.put(EActionType._3pp, po.getThreePm() / Math.max(1, po.getThreePa()));
        this.ability.put(EActionType.reb, po.getOreb() + po.getDreb());
        this.ability.put(EActionType.oreb, po.getOreb());
        this.ability.put(EActionType.dreb, po.getDreb());
        this.ability.put(EActionType.ast, po.getAst());
        this.ability.put(EActionType.stl, po.getStl());
        this.ability.put(EActionType.blk, po.getBlk());
        this.ability.put(EActionType.to, po.getTo());
        this.ability.put(EActionType.min, po.getMin());
        this.ability.put(EActionType.pf, po.getPf());

        this.ability.put(EActionType.ocap, (float) po.getAttrCap());
        this.ability.put(EActionType.dcap, (float) po.getGuaCap());
        this.ability.put(EActionType.skill_power, (float) po.getSkill());

        this.ability.put(EActionType.power, 100f);
        this.ability.put(EActionType.power_rate, powerRate + 0f);

        //
        if (avg != null) {
            this.avgAbility.put(EActionType.fgm, avg.getFgm());
            this.avgAbility.put(EActionType.ftm, avg.getFtm());
            this.avgAbility.put(EActionType.pts, avg.getPts());
            this.avgAbility.put(EActionType._3pm, avg.getThreePm());
            this.avgAbility.put(EActionType._3pa, avg.getThreePa());
            this.avgAbility.put(EActionType._3pp, avg.getThreePm() / Math.max(1, avg.getThreePa()));
            this.avgAbility.put(EActionType.reb, avg.getOreb() + avg.getDreb());
            this.avgAbility.put(EActionType.oreb, avg.getOreb());
            this.avgAbility.put(EActionType.dreb, avg.getDreb());
            this.avgAbility.put(EActionType.ast, avg.getAst());
            this.avgAbility.put(EActionType.stl, avg.getStl());
            this.avgAbility.put(EActionType.blk, avg.getBlk());
            this.avgAbility.put(EActionType.to, avg.getTo());
            this.avgAbility.put(EActionType.min, avg.getMin());
            this.avgAbility.put(EActionType.pf, avg.getPf());
        }
        
        // 新秀数据
        if(po.getDraft() != null && !po.getDraft().equals("")) {
        	this.draft = Arrays.stream(po.getDraft().split("_"))
        			.mapToInt(i-> Integer.valueOf(i)).toArray();
        }
    }

    public PlayerBean(PlayerBeanVO avg) {
        this.avgAbility = Maps.newHashMap();
        if (avg != null) {
            this.avgAbility.put(EActionType.fgm, avg.getFgm());
            this.avgAbility.put(EActionType.ftm, avg.getFtm());
            this.avgAbility.put(EActionType.pts, avg.getPts());
            this.avgAbility.put(EActionType._3pm, avg.getThreePm());
            this.avgAbility.put(EActionType._3pa, avg.getThreePa());
            this.avgAbility.put(EActionType._3pp, avg.getThreePm() / Math.max(1, avg.getThreePa()));
            this.avgAbility.put(EActionType.reb, avg.getOreb() + avg.getDreb());
            this.avgAbility.put(EActionType.oreb, avg.getOreb());
            this.avgAbility.put(EActionType.dreb, avg.getDreb());
            this.avgAbility.put(EActionType.ast, avg.getAst());
            this.avgAbility.put(EActionType.stl, avg.getStl());
            this.avgAbility.put(EActionType.blk, avg.getBlk());
            this.avgAbility.put(EActionType.to, avg.getTo());
            this.avgAbility.put(EActionType.min, avg.getMin());
            this.avgAbility.put(EActionType.pf, avg.getPf());
        }
    }

    public Map<EActionType, Float> getAvgAbility() {
        return avgAbility;
    }

    private void initPosition(String p) {
        String[] ps = StringUtil.toStringArray(p, "[/]");
        this.position[0] = EPlayerPosition.valueOf(ps[0]);
        if (ps.length >= 2) {
            this.position[1] = EPlayerPosition.valueOf(ps[1]);
        } else {
            this.position[1] = EPlayerPosition.NULL;
        }
    }

    public String getEnName() {
        return enName;
    }

    public int getBeforeCap() {
        return beforeCap;
    }

    public ENBAPlayerTeam getTeam() {
        return team;
    }

    public int getTeamId() {
        return team.getTid();
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public EPlayerGrade getGrade() {
        return grade;
    }

    public int getBeforePrice() {
        return beforePrice;
    }

    public ENBAPlayerTitle getPlayerTitle() {
        return playerTitle;
    }

    public boolean isInjured() {
        return injured;
    }

    public int getPrice() {
        return price;
    }

    public float getAbility(EActionType type) {
        return ability.getOrDefault(getRealType(type), 0f);
    }

    public static EActionType getRealType(EActionType type) {
        switch (type) {
            case ft_act:
                return EActionType.ftm;
            case _3p:
                return EActionType._3pm;
        }
        return type;
    }

    public float getAvgAbility(EActionType type) {
        return avgAbility.getOrDefault(getRealType(type), 0f);
    }

    public void initAbility(PlayerAbility pa) {
        this.ability.forEach((k, v) -> pa.setAttr(k, v));

    }

    public Map<EActionType, Float> getAbility() {
        return this.ability;
    }

    public int getPlayerRid() {
        return resourceId;
    }

    public EPlayerPosition[] getPosition() {
        return position;
    }

	public int[] getDraft() {
		return draft;
	}

}
