package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple3;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple3;
import com.ftkj.enums.EActionType;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/** 主线赛程. 星级胜利条件 */
public class MMatchConditionBean extends AbstractExcelBean {
    /** 配置id */
    private int id;
    /** 星级条件类型 */
    private StarConditionType type;
    /** 比赛数据值 vi1 */
    private int vi1;
    /** 比赛数据值 vi2 */
    private int vi2;
    /** 比赛数据值 vi3 */
    private int vi3;
    /** 比赛数据对 */
    private Map<EActionType, ActionCondition> conditions;

    @Override
    public void initExec(RowData row) {
        this.type = StarConditionType.convert(getInt(row, "type_"));
        //winTupleAct1	winTupleOp1	winTupleVal1
        //数据要求1. 数据类型	数据要求1. 操作符	数据要求1. 数据值
        //int	string	int
        this.conditions = parseActionConditions(row, "actName", "actOp", "actVal");
    }

    public static Map<EActionType, ActionCondition> parseActionConditions(RowData row, String headAct, String headOp, String headValue) {
        return parseActionConditions(row, headAct, headOp, headValue, null);
    }

    public static Map<EActionType, ActionCondition> parseActionConditions(RowData row,
                                                                          String headAct,
                                                                          String headOp,
                                                                          String headValue,
                                                                          Predicate<EActionType> actFilter) {
        //act1	op1	v1
        //数据要求1. 数据类型	数据要求1. 操作符	数据要求1. 数据值
        //int	string	int
        IDListTuple3<String, String, String, Integer> ltp =
                ParseListColumnUtil.parse(row, toStr, headAct, toStr, headOp, toStr, headValue, toInt);
        Map<EActionType, ActionCondition> actConditions = new EnumMap<>(EActionType.class);
        for (IDTuple3<String, String, String, Integer> tp : ltp.getTuples().values()) {
            if (tp.getE1() == null || tp.getE1().isEmpty()) {
                continue;
            }
            EActionType act = EActionType.convertByName(tp.getE1());
            Objects.requireNonNull(act, "act name : " + tp.getE1());
            if (actFilter != null && !actFilter.test(act)) {
                continue;
            }
            CompareOp op = CompareOp.convert(tp.getE2());
            actConditions.put(act, new ActionCondition(act, op, tp.getE3()));
        }
        return ImmutableMap.copyOf(actConditions);
    }

    public int getId() {
        return id;
    }

    public StarConditionType getType() {
        return type;
    }

    public int getVi1() {
        return vi1;
    }

    public int getVi2() {
        return vi2;
    }

    public int getVi3() {
        return vi3;
    }

    public Map<EActionType, ActionCondition> getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"type\":" + type +
                ", \"tname\":" + type.name() +
                ", \"vi1\":" + vi1 +
                ", \"vi2\":" + vi2 +
                ", \"vi3\":" + vi3 +
                ", \"conditions\":" + conditions +
                '}';
    }

    public String baseInfo() {
        return "{" +
                "\"id\":" + id +
                ", \"type\":" + type +
                ", \"tname\":" + type.name() +
                ", \"v1\":" + vi1 +
                ", \"v2\":" + vi2 +
                ", \"v3\":" + vi3 +
                '}';
    }

    /**
     * 星级条件.
     * <pre>
     * 二星条件（球队型）
     * 球队赢A分
     * 比赛进入加时并获胜
     * 绝杀比赛获胜
     * 球队分数达到A分并获胜
     * 落后A分反超获胜
     *
     * 三星条件（球员型）                                	排名规则
     * N名球员得分M	                                    取这N名球员的得分总和，总和高优先。
     * N名球员助攻M个	                                取这N名球员的助攻个数总和，总和高优先。
     * N名球员篮板M个	                                取这N名球员的篮板个数总和，总和高优先。
     * N名球员抢断M个	                                取这N名球员的抢断个数总和，总和高优先。
     * N名球员盖帽M个	                                取这N名球员的盖帽个数总和，总和高优先。
     * N名球员三分命中M个	                            取这N名球员的三分命中个数总和，总和高优先。
     * N名球员罚球命中M个	                            取这N名球员的罚球命中个数总和，总和高优先。
     * 任意一名球员得分N、篮板M个、助攻O个，抢断P个，
     * 盖帽Q个，三分R个，罚球S个（7项数据任填，可能只有2项）	每1得分/篮板/助攻/抢断/盖帽/三分命中个数/罚球命中个数/分别获得1/3/6/9/7/3/1评分，总评分高优先。（只取配置的项）
     * N名球员投篮不少于M次且投篮命中率100%	取这N名球员的投篮次数总和，总和高优先。
     * N名球员罚球不少于M次且罚球命中率100%	取这N名球员的罚球次数总和，总和高优先。
     * N名球员三分不少于M次且三分命中率100%	取这N名球员的三分次数总和，总和高优先。
     * 一名球员单节拿到N分	单节拿的分高优先
     * N名球员获得两双数据（两双定义：常规五项数据其中之2）	每1得分/篮板/助攻/抢断/盖帽分别获得1/3/6/9/7评分，取这N名球员的总评分总和，总和高优先。（5项都加）
     * N名球员获得三双数据（三双定义：常规五项数据其中之3）	每1得分/篮板/助攻/抢断/盖帽分别获得1/3/6/9/7评分，取这N名球员的总评分总和，总和高优先。（5项都加）
     * 每位球员失误数不超过N个	取全队失误总和，失误低的优先
     * 每位球员犯规数不超过N个	取全队犯规总和，犯规低的优先
     * 一名球员打铁M个	取打铁数最高的参与排名，打铁数高的优先
     * 一名球员得分N且出手不超过M次	取出手不少于M次的最高得分参与排名，得分高的优先
     * 一名球员得分N且犯规不超过M个	取犯规不超过M个的最高得分参与排名，得分高的优先
     * 一名球员得分N且失误不超过M个	取失误不超过M个的最高得分参与排名，得分高的优先
     *
     * </pre>
     */
    public enum StarConditionType {
        //（球队型）
        /** 球队赢 vi1 分 */
        Team_Win_Point(1),
        /** 比赛进入加时并获胜 */
        Team_Overtime_Win(2),
        /** 绝杀比赛获胜 */
        Team_Last_Shot(3),
        /** 球队获胜并达到 vi1 分 */
        Team_Point_Win(4),
        /** 落后 vi1 分反超获胜 */
        Team_Comeback(5),
        //（球员型）
        /**
         * vi1 名球员每人满足 N 个 tuple 数据对(类型和值)中任意 vi2 个.
         * <pre>
         * tuple = 数据类型:比较类型(<, <=, >, >=, =):数据值,数据类型:比较类型:数据值
         * </pre>
         * <pre>
         *  任意一名球员得分N、篮板M个、助攻O个，抢断P个，盖帽Q个，三分R个，罚球S个（7项数据任填，可能只有2项）
         *
         *  N名球员获得两双数据（两双定义：常规五项数据其中之2）
         *  N名球员获得三双数据（三双定义：常规五项数据其中之3）
         * </pre>
         */
        Multi_Player_Any_Num_Action_Type(20),
        /**
         * vi1 名球员每人满足 N 个 tuple 数据对(类型和值)
         * <pre>
         * tuple = 数据类型:比较类型(<, <=, >, >=, =):数据值,数据类型:比较类型:数据值
         * </pre>
         * <pre>
         *  N名球员得分M
         *  N名球员助攻M个
         *  N名球员篮板M个
         *  N名球员抢断M个
         *  N名球员盖帽M个
         *  N名球员三分命中M个
         *  N名球员罚球命中M个
         *
         *  一名球员打铁M个
         *
         *  N名球员投篮不少于M次且投篮命中率100%
         *  N名球员罚球不少于M次且罚球命中率100%
         *  N名球员三分不少于M次且三分命中率100%
         *
         *  一名球员得分N且出手不超过M次
         *  一名球员得分N且犯规不超过M个
         *  一名球员得分N且失误不超过M个
         * </pre>
         */
        Multi_Player_Multi_Action_Type(30),
        /**
         * vi1 名球员每人在任意一节中满足 N 个 tuple 数据对(类型和值)
         * <pre>
         * tuple = 数据类型:比较类型(<, <=, >, >=, =):数据值,数据类型:比较类型:数据值
         * </pre>
         * <pre>
         *  一名球员单节拿到N分
         * </pre>
         */
        Multi_Player_Any_Num_Action_Type_Of_Any_Quarter(40),
        /**
         * 所有上场球员每人满足 N 个 tuple 数据对(类型和值)
         * <pre>
         * tuple = 数据类型:比较类型(<, <=, >, >=, =):数据值,数据类型:比较类型:数据值
         * </pre>
         * <pre>
         *  每位球员失误数不超过N个
         *  每位球员犯规数不超过N个
         * </pre>
         */
        All_Player_Any_Num_Action_Type(50),

        //
        ;
        final int type;

        StarConditionType(int type) {
            this.type = type;
        }

        public StarConditionType getActionType() {
            return null;
        }

        public int getType() {
            return type;
        }

        public static final Map<Integer, StarConditionType> cache = new HashMap<>();

        static {
            for (StarConditionType et : StarConditionType.values()) {
                cache.put(et.getType(), et);
            }
        }

        public static StarConditionType convert(int type) {
            return cache.get(type);
        }

        @Override
        public String toString() {
            return String.valueOf(type);
        }
    }
}
