package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.EActionType;
import com.ftkj.util.excel.RowData;

import java.io.Serializable;

/**
 * 比赛子行为配置
 *
 * @author luch
 */
public class BaseSubActionBean implements SubActionBean, Serializable {
    private static final long serialVersionUID = -1791925665634189556L;
    /** 球员进攻技能 */
    public static final int Vi1_Player_SKill_Attack = 1;
    private final int id;
    /** 行为类型 */
    private final EActionType action;
    /**
     * 行为概率(1-100)(0或不填使用默认规则).
     * 目前可以控制如下行为:
     * 盖帽时 : 盖帽球员所在球队获得球权的概率
     * 罚球时 : 命中概率(不填或0时则使用球员罚球能力)
     * 篮板时 : 篮板球员所在球队获得球权的概率
     */
    private final int chance;
    /** 子行为附加参数 int1 使用球员或教练技能时的技能id, 更换战术时的战术id */
    private final int vi1;
    /** 子行为附加参数 2  更换战术时的战术防守id */
    private final int vi2;
    /** 执行此行为的球队, 不为 null 时强制选择执行方, 为 null 时按业务逻辑选择 */
    private final HomeAway homeAway;

    public BaseSubActionBean(int id, EActionType action, int chance, int vi1, int vi2, HomeAway homeAway) {
        this.id = id;
        this.action = action;
        this.chance = chance;
        this.vi1 = vi1;
        this.vi2 = vi2;
        this.homeAway = homeAway;
    }

    @Override
    public final int getId() {
        return id;
    }

    @Override
    public final EActionType getAction() {
        return action;
    }

    @Override
    public final int getChance() {
        return chance;
    }

    @Override
    public final int getChance(int defaultValue) {
        return chance > 0 ? chance : defaultValue;
    }

    @Override
    public int getVi1() {
        return vi1;
    }

    @Override
    public int getVi2() {
        return vi2;
    }

    @Override
	public HomeAway getHomeAway() {
        return homeAway;
    }

    @Override
    public boolean isHome() {
        return HomeAway.home == homeAway;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"act\":" + action +
                ", \"c\":" + chance +
                ", \"vi1\":" + vi1 +
                ", \"vi2\":" + vi2 +
                ", \"ha\":" + homeAway +
                '}';
    }

    public static class Builder extends AbstractExcelBean {
        protected int id;
        /** 行为类型 */
        protected EActionType action;
        /** 执行此行为的球队, 不为 null 时强制选择执行方, 为 null 时按业务逻辑选择 */
        protected HomeAway ha;
        /**
         * 行为概率(1-100)(0或不填使用默认规则).
         * 目前可以控制如下行为:
         * 盖帽时 : 盖帽球员所在球队获得球权的概率
         * 罚球时 : 命中概率(不填或0时则使用球员罚球能力)
         * 篮板时 : 篮板球员所在球队获得球权的概率
         */
        protected int chance;
        /** 子行为附加参数 int1 */
        protected int vi1;
        /** 子行为附加参数 int2 */
        protected int vi2;

        public int getId() {
            return id;
        }

        @Override
        public void initExec(RowData row) {
            this.action = EActionType.convertByName(getStr(row, getActionName()));
            this.ha = HomeAway.convert(getStr(row, getHomeAwayName()));
        }

        protected String getActionName() {
            return "type";
        }

        protected String getHomeAwayName() {
            return "team";
        }

        public BaseSubActionBean build() {
            return new BaseSubActionBean(id, action, chance, vi1, vi2, ha);
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"action\":" + action +
                    ", \"ha\":" + ha +
                    ", \"chance\":" + chance +
                    ", \"vi1\":" + vi1 +
                    ", \"vi2\":" + vi2 +
                    '}';
        }
    }
}
