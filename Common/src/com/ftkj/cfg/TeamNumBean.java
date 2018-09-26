package com.ftkj.cfg;

import java.util.HashMap;
import java.util.Map;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ExcelParseUtil;
import com.ftkj.cfg.base.ExcelParseUtil.IntervalAndVal;
import com.ftkj.enums.EMoneyType;
import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;

/** 球队次数及购买次数消耗配置 */
public class TeamNumBean {
    /** 按业务规则来 */
    public static final int Reset_By_Bus = 0;
    /** 只增加,不重置 */
    public static final int Reset_OnlyIncre = 1;
    /** 每日重置 */
    public static final int Reset_Daily = 2;
    /** 数值类型 */
    private final TeamNumType numType;
    /** 最大值 */
    private final int maxNum;
    /**
     * 重置规则:
     * 0或不填: 按业务规则来
     * 1: 只增加,不重置
     * 2: 每日重置
     */
    private final int resetMode;
    /** 消耗的货币类型 */
    private final EMoneyType currencyType;
    /** 次数及对应的消耗 */
    private final ImmutableList<NumAndCurrency> numberAndCurrencies;

    public TeamNumBean(TeamNumType numType,
                       int maxNum,
                       int resetMode,
                       EMoneyType currencyType,
                       ImmutableList<NumAndCurrency> numberAndCurrencies) {
        this.numType = numType;
        this.maxNum = maxNum;
        this.resetMode = resetMode;
        this.currencyType = currencyType;
        this.numberAndCurrencies = numberAndCurrencies;
    }

    public static final class Builder extends AbstractExcelBean {
        /** 数值类型 */
        private int numType;
        /** 最大值 */
        private int maxNum;
        /**
         * 重置规则:
         * 0或不填: 按业务规则来
         * 1: 只增加,不重置
         * 2: 每日重置
         */
        private int resetMode;
        /** 消耗的货币类型 */
        private String currencyType;
        /** 次数及对应的消耗 */
        private ImmutableList<NumAndCurrency> numberAndCurrencies;

        @Override
        public void initExec(RowData row) {
            //            numLower1	numUpper1	currency1
            //            开始（闭区间，后面相同）	结束(闭区间，后面相同）	消耗货币数量
            //            int	int	int
            numberAndCurrencies = ExcelParseUtil.parseIntervalAndValue(row, "numLower", "numUpper", "numCurr",
                    NumAndCurrency::new);
        }

        public TeamNumBean build() {
            return new TeamNumBean(TeamNumType.convertByType(numType),
                    maxNum,
                    resetMode,
                    EMoneyType.convertByName(currencyType),
                    numberAndCurrencies);
        }
    }

    public TeamNumType getNumType() {
        return numType;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public int getResetMode() {
        return resetMode;
    }

    public boolean isDailyReset() {
        return resetMode == TeamNumBean.Reset_Daily;
    }

    public EMoneyType getCurrencyType() {
        return currencyType;
    }

    public ImmutableList<NumAndCurrency> getNumberAndCurrencies() {
        return numberAndCurrencies;
    }

    public Integer getCurrency(int num) {
        int index = BinarySearch.binarySearch(numberAndCurrencies, num, (nc, key) ->
                IntervalInt.compare(nc.getInterval(), key));
        if (index < 0) {
            return 0;
        }
        return numberAndCurrencies.get(index).getCurrency();
    }

    /** 次数和消耗的货币 */
    public static final class NumAndCurrency extends IntervalAndVal {
        private static final long serialVersionUID = -7404982714538819792L;

        public NumAndCurrency(IntervalInt num, int currency) {
            super(num, currency);
        }

        public int getCurrency() {
            return getValue();
        }
    }

    /** 球队次数类型 */
    public enum TeamNumType {
        /** 主线副本. 比赛次数 */
        Main_Match_Num(36003),
        /** 竞技场. 已购买付费比赛次数 */
        Arena_Buy_Match_Num(39000),
        /** 竞技场. 重置比赛cd次数 */
        Arena_Reset_CD_Num(39001),
        /** 联盟赛. 挑战加速次数 */
        League_Speed_Num(40000),
        /** 新秀对抗赛次数购买消耗*/
        Dual_Meet_Buy_Num(50001),
        /** 新秀排位赛次数购买消耗*/
        Dual_Meet_Rank_Buy_Num(50002),
        /** 全明星赛激励价格*/
        All_Star_Jili_Num(50003),
        /** 全明星赛次数购买价格*/
        All_Star_Buy_Num(50004),
        /** 极限挑战次数购买消耗*/
        limit_challenge_buy_num(50005),
        /** 球星荣耀购买次数*/
        honorMatch(50006),
        ;
        
        TeamNumType(int type) {
            this.type = type;
        }

        private final int type;

        public int getType() {
            return type;
        }

        public static final Map<Integer, TeamNumType> typeCaches = new HashMap<>();

        static {
            for (TeamNumType t : values()) {
                if (typeCaches.containsKey(t.getType())) {
                    throw new Error("duplicate action type :" + t.getType());
                }
                typeCaches.put(t.getType(), t);
            }
        }

        public static TeamNumType convertByType(int type) {
            return typeCaches.get(type);
        }

    }
}
