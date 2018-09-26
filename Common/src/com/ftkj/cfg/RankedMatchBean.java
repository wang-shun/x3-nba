package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.excel.RowData;

import java.time.LocalDateTime;

/** 天梯赛. 配置 */
public class RankedMatchBean {

    /** 天梯赛. 赛季配置 */
    public static final class RankedMatchSeasonBean extends AbstractExcelBean {
        /** 赛季id */
        private int id;
        /** 上一个赛季 */
        private int preId;
        /** 下一赛季 */
        private int nextId;
        private LocalDateTime startLdt;
        /** 开始时间(yyyy-mm-dd hh:mm:ss 或者 yyyy-mm-dd) */
        private long start;
        private LocalDateTime endLdt;
        /** 结束时间 */
        private long end;

        @Override
        public void initExec(RowData row) {
            String startStr = row.get("start_");
            String endStr = row.get("end_");
            startLdt = DateTimeUtil.parseToLdt(startStr);
            start = DateTimeUtil.toMillis(startLdt);
            endLdt = DateTimeUtil.parseToLdt(endStr);
            end = DateTimeUtil.toMillis(endLdt);
        }

        public int getId() {
            return id;
        }

        public int getPreId() {
            return preId;
        }

        public void setPreId(int preId) {
            this.preId = preId;
        }

        public int getNextId() {
            return nextId;
        }

        public LocalDateTime getStartLdt() {
            return startLdt;
        }

        public long getStart() {
            return start;
        }

        public LocalDateTime getEndLdt() {
            return endLdt;
        }

        public long getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"preId\":" + preId +
                    ", \"nextId\":" + nextId +
                    ", \"startLdt\":" + startLdt +
                    ", \"start\":" + start +
                    ", \"endLdt\":" + endLdt +
                    ", \"end\":" + end +
                    '}';
        }
    }

    /** 连胜系数 */
    public static final class WinningStreakBean extends AbstractExcelBean {
        /** 连胜次数 */
        private IntervalInt num;
        /** 连胜系数 */
        private float factor;

        @Override
        public void initExec(RowData row) {
            int min = getInt(row, "min");
            int max = getInt(row, "max");
            this.num = new IntervalInt(min, max);
        }

        public IntervalInt getNum() {
            return num;
        }

        public float getFactor() {
            return factor;
        }
    }

    /** 赛季起始积分转换规则 */
    public static final class RatingConvertBean extends AbstractExcelBean {
        /** 积分要求 */
        private IntervalInt rating;
        /** 新赛季最大积分 */
        private int value;

        @Override
        public void initExec(RowData row) {
            int min = getInt(row, "min");
            int max = getInt(row, "max");
            this.rating = new IntervalInt(min, max);
        }

        public IntervalInt getRating() {
            return rating;
        }

        public int getValue() {
            return value;
        }
    }

    /** 赛季首次奖励配置 */
    public static final class FirstAwardBean {
        /** 赛季首次奖励配置id(1至64) */
        private int id;
        /** 要求的最少积分 */
        private int rating;
        /** 奖励掉落包 */
        private int drop;

        public int getId() {
            return id;
        }

        public int getRating() {
            return rating;
        }

        public int getDrop() {
            return drop;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"rating\":" + rating +
                    ", \"drop\":" + drop +
                    '}';
        }
    }
}
