package com.ftkj.cfg.base;

import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple3;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple3;
import com.ftkj.console.BeanException;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static com.ftkj.cfg.base.ParseListColumnUtil.toInt;

public class ExcelParseUtil {

    public static <T extends IntervalAndVal> ImmutableList<T>
    parseIntervalAndValue(RowData row,
                          String prefix1,
                          String prefix2,
                          String prefix3,
                          BiFunction<IntervalInt, Integer, T> supplier) {
        //            numLower1	numUpper1	currency1
        //            开始（闭区间，后面相同）	结束(闭区间，后面相同）	消耗货币数量
        //            int	int	int
        IDListTuple3<Integer, Integer, Integer, Integer> ltp =
                ParseListColumnUtil.parse(row, toInt, prefix1, toInt, prefix2, toInt, prefix3, toInt);
        List<T> temp = new ArrayList<>();
        int maxNum = 0;
        for (IDTuple3<Integer, Integer, Integer, Integer> tp : ltp.getTuples().values()) {
            if (tp.getE1() <= 0) {
                continue;
            }
            Integer lower = tp.getE1();
            Integer upper = tp.getE2();
            IntervalInt numii = new IntervalInt(lower, upper);
            if (upper > maxNum) {
                maxNum = upper;
            }
            temp.add(supplier.apply(numii, tp.getE3()));
        }
        temp.sort(Comparator.comparing(IntervalAndVal::getInterval));//按区间升序排列
        return ImmutableList.copyOf(temp);
    }

    public static <T extends IntervalAndVal> void validate(ImmutableList<T> intervals,
                                                           String msg, Object... args) {
        String preMsg = String.format(msg, args);
        for (int i = 0; i < intervals.size(); i++) {
            IntervalAndVal curr = intervals.get(i);
            if (i == 0) {
                if (curr.getInterval().getLower() != 1) {
                    throw BeanException.exception(preMsg + "第一次没有配置", args);
                }
            } else {
                IntervalAndVal pre = intervals.get(i - 1);
                int expectPre = curr.getInterval().getLower() - 1;
                IntervalInt preII = pre.getInterval();
                IntervalInt currII = curr.getInterval();
                if (preII.getUpper() < expectPre) {
                    throw BeanException.exception(preMsg + "第 %s 次没有配置. 上一个区间 %s, 本区间 %s 不连续",
                            currII.getLower() - 1, pre.getInterval(), curr.getInterval());
                }
                if (preII.getUpper() > expectPre) {
                    throw BeanException.exception(preMsg + " 第 %s 次重复配置. 上一个区间 %s, 本区间 %s 区间重复",
                            currII.getLower() - 1, pre.getInterval(), curr.getInterval());
                }
            }
        }
    }

    /** 区间和关联的值 */
    public static class IntervalAndVal implements Serializable {
        private static final long serialVersionUID = 8693006894610925385L;
        private final IntervalInt interval;
        private final int value;

        public IntervalAndVal(IntervalInt interval, int value) {
            this.interval = interval;
            this.value = value;
        }

        public IntervalInt getInterval() {
            return interval;
        }

        public int getValue() {
            return value;
        }
    }
}
