package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple4;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 主线赛程赛区配置.
 */
public class MMatchDivisionBean extends AbstractExcelBean {
    public static final int STAR_IDX_MIN = 1;
    public static final int STAR_IDX_MAX = 63;
    /** 1:需要前置赛区通关所有关卡 */
    public static final int PRE_DIV_STRATEGY_ENABLE_ALL = 1;
    /** 赛区配置id */
    private int id;
    /** 赛区类型(1:常规赛,2:精英) */
    //    private int type;
    /** 星级礼包配置 */
    private Map<Integer, StarAward> starAwards = Collections.emptyMap();
    /** 开启本赛区需要的前置赛区的id列表 */
    private Set<Integer> enablePreIds;
    /** 开启本赛区是否需要前置赛区规则(0或不填:无条件,需要前置赛区开启, 1:需要前置赛区通关所有关卡) */
    private int preDivStrategy;
    /** 开启本赛区需要的前置赛区关卡星级数量 */
    private int enablePreStar;
    /** 后续赛区 */
    private Set<Integer> nextDivs = new LinkedHashSet<>();

    @Override
    public void initExec(RowData row) {
        //        this.type = LevType.convert(getInt(row, "type_"));
        this.preDivStrategy = getInt(row, "enablePreAll_");
        this.enablePreIds = ImmutableSet.copyOf(StringUtil.toIntegerArray(row.get("enablePreId"), StringUtil.ST));
        IDListTuple4<Integer, Integer, Integer, String, Integer> tp =
                ParseListColumnUtil.parse(row, toInt, "starIdx", toInt, "starNum", toInt, "starItem", toStr, "starDrop", toInt);

        Map<Integer, StarAward> sas = new LinkedHashMap<>();
        for (int i = 0; i < tp.getL1().size(); i++) {
            sas.put(tp.getL1().get(i),
                    new StarAward(tp.getL1().get(i),
                            tp.getL2().get(i),
                            PropSimple.parseItems(tp.getL3().get(i)),
                            tp.getL4().get(i)));
        }
        this.starAwards = sas;
    }

    public int getId() {
        return id;
    }

    //    public int getType() {
    //        return type;
    //    }

    public Map<Integer, StarAward> getStarAwards() {
        return starAwards;
    }

    public StarAward getStarAwards(int starIdx) {
        return starAwards.get(starIdx);
    }

    public Set<Integer> getEnablePreIds() {
        return enablePreIds;
    }

    public boolean hasEnablePre() {
        return enablePreIds != null && !enablePreIds.isEmpty();
    }

    public int getPreDivStrategy() {
        return preDivStrategy;
    }

    public int getEnablePreStar() {
        return enablePreStar;
    }

    public Set<Integer> getNextDivs() {
        return nextDivs;
    }

    /** 星级礼包奖励配置 */
    public static final class StarAward {
        /** 星级礼包的id, 配置之后不能修改, 存储到数据库. (预留 1-64) */
        private final int id;
        /** 星级礼包需要的星星数量 */
        private final int num;
        /** 星级礼包的奖励(道具) */
        private final List<PropSimple> props;
        /** 星级礼包的奖励(掉落) */
        private final int drop;

        public StarAward(int id, int num, List<PropSimple> props, int drop) {
            this.id = id;
            this.num = num;
            this.props = props;
            this.drop = drop;
        }

        public int getId() {
            return id;
        }

        public int getNum() {
            return num;
        }

        public List<PropSimple> getProps() {
            return props;
        }

        public int getDrop() {
            return drop;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"num\":" + num +
                    ", \"items\":" + props +
                    ", \"drop\":" + drop +
                    '}';
        }
    }

    //    /** 赛区类型 */
    //    public enum LevType {
    //        /** 常规赛 */
    //        Regular(1),
    //        /** 2 精英 */
    //        Elite(2);
    //
    //        private final int type;
    //
    //        LevType(int type) {
    //            this.type = type;
    //        }
    //
    //        public int getType() {
    //            return type;
    //        }
    //
    //        public static LevType convert(int type) {
    //            switch (type) {
    //                case 1: return Regular;
    //                case 2: return Elite;
    //            }
    //            return null;
    //        }
    //    }

}
