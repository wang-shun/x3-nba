package com.ftkj.util;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Delayed;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;

public class DateTimeUtil {
    /** 秒(毫秒) */
    public static final int SEC = 1000;
    /** 3秒(毫秒) */
    public static final int _3SEC = 3000;
    /** 秒(毫秒) */
    public static final int SECOND = 1000;
    /** 半分钟(毫秒) */
    public static final int HALF_MIN = 30 * SECOND;
    /** 分钟(毫秒) */
    public static final int MINUTE = 60 * SECOND;
    /** 时(毫秒) */
    public static final int HOUR = 60 * MINUTE;
    /** 天(毫秒) */
    public static final int DAILY = 24 * HOUR;
    /** 周(毫秒) */
    public static final int WEEK = 7 * DAILY;
    /** 月(毫秒) */
    public static final int MONTH = 30 * DAILY;
    /** 大约1年(毫秒) */
    public static final int YEAR = 365 * DAILY;
    /** 5小时 */
    public static final int _5_HOUR = 5 * HOUR;

    public static DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static DateTimeFormatter ymdhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter mdhm = DateTimeFormatter.ofPattern("MM-dd HH:mm");
    public static DateTimeFormatter hms = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static DateTimeFormatter y_m_d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter ymdhm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /** 系统默认 ZoneOffset */
    public static final ZoneOffset DEFAULT_ZONE_OFFSET = OffsetDateTime.now().getOffset();
    public static final DateTimeFormatter LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .optionalStart()
        .optionalStart()
        .appendLiteral(' ')
        .optionalEnd()
        .optionalStart()
        .optionalEnd()
        .appendOptional(DateTimeFormatter.ISO_TIME)
        .toFormatter();

    public static void main(String[] args) {
        System.out.println(parseToLdt("2018-02-01"));
        System.out.println(parseToLdt("2018-02-01 01:00"));
        System.out.println(parseToLdt("2018-02-01 01:01:02"));
        LocalDateTime x = parseToLdt("2018-07-10 18:49:46");
        long curr = toMillis(x);
        System.out.printf("curr %s - 0 = %s\n", curr, (int) curr);
        System.out.println(difTimeMill(0));
    }

    /** formatter : yyyy-MM-dd[[ ]HH:mm[:ss] */
    public static LocalDateTime parseToLdt(String str) {
        TemporalAccessor ta = LOCAL_DATE_TIME.parseBest(str, LocalDateTime::from, LocalDate::from);
        if (ta instanceof LocalDateTime) {
            return (LocalDateTime) ta;
        } else if (ta instanceof LocalDate) {
            LocalDate ld = (LocalDate) ta;
            return LocalDateTime.of(ld, LocalTime.MIDNIGHT);
        } else {
            return LocalDateTime.class.cast(ta);
        }
    }

    /**
     * 字符串转换成本地时间
     *
     * @param str
     * @return
     */
    public static DateTime parseToLdtDateTime(String str) {
        return new DateTime(Date.from(parseToLdt(str).atZone(ZoneId.systemDefault()).toInstant()).getTime());
    }

    /**
     * 两时间差天数 d2 - d1
     *
     * @param d1   时间1
     * @param d2   时间2
     * @param hour 小时数基准，0则是以每日0点来计算，分钟，秒数都会重置成0
     * @return 相差天数
     */
    public static int getDaysBetweenNum(DateTime d1, DateTime d2, int hour) {
        d1 = d1.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(hour);
        d2 = d2.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(hour);
        return Days.daysBetween(d1, d2).getDays();
    }

    /**
     * 两时间差天数 d2 - d1
     *
     * @param d1 时间1
     * @param d2 时间2
     * @return 相差天数
     */
    public static int getDaysBetweenNum(DateTime d1, DateTime d2) {
        return Days.daysBetween(d1, d2).getDays();
    }

    /**
     * 一般用做时间某时间差的开始天数，也就是第一天时间开始天数是1这种情况调用
     * 两时间差天数
     *
     * @param d1   时间1
     * @param d2   时间2
     * @param hour 小时数基准，0则是以每日0点来计算，分钟，秒数都会重置成0
     * @return 相差天数 + 1
     */
    public static int getDaysStartNum(DateTime d1, DateTime d2, int hour) {
        return getDaysBetweenNum(d1, d2, hour) + 1;
    }

    /**
     * 两个自然月的时间差，如10和N号和9月N号都是相差一个月
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int getMonthsBetweenNum(DateTime d1, DateTime d2) {
        d1 = d1.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1);
        d2 = d2.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1);
        return Months.monthsBetween(d1, d2).getMonths();
    }

    public static String getNow() {
        return getString(DateTime.now(), ymd);
    }

    public static String getNowTime() {
        return getString(DateTime.now(), ymdhms);
    }

    public static String getNowTimeDate() {
        return getString(DateTime.now(), mdhm);
    }

    public static String getNowTimeDateHms() {
        return getString(DateTime.now(), hms);
    }

    public static String getNowTimeDateYmdhm() {
        return getString(DateTime.now(), ymdhm);
    }

    public static DateTime getDateTime(String time) {
        LocalDateTime date = LocalDateTime.parse(time, ymdhms);
        return DateTime.now().withYear(date.getYear()).withMonthOfYear(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth())
            .withHourOfDay(date.getHour()).withMinuteOfHour(date.getMinute()).withSecondOfMinute(date.getSecond());
    }

    public static DateTime getDateTimeYmd(String time) {
        LocalDateTime date = LocalDateTime.parse(time, y_m_d);
        return DateTime.now().withYear(date.getYear()).withMonthOfYear(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth())
            .withHourOfDay(date.getHour()).withMinuteOfHour(date.getMinute()).withSecondOfMinute(date.getSecond());
    }

    public static String getString(DateTime dt, DateTimeFormatter dtf) {
        LocalDateTime ldt = LocalDateTime.of(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dt.getHourOfDay(), dt.getMinuteOfHour(), dt.getSecondOfMinute());
        return ldt.format(dtf);
    }

    /**
     * 转换格式 yyyyMMdd 字符串
     *
     * @param dt
     * @return
     */
    public static String getString(DateTime dt) {
        return getString(dt, ymd);
    }

    /**
     * 转换格式 yyyyMMdd 字符串
     *
     * @param dt
     * @return
     */
    public static String getStringSql(DateTime dt) {
        return getString(dt, ymdhms);
    }

    /**
     * 转换格式
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return 计算date到当前时间之间相差多少秒
     */
    public static int secondBetween(Date date) {
        int second = Seconds.secondsBetween(new DateTime(date), DateTime.now()).getSeconds();
        return second;
    }

    /**
     * @param date
     * @return 计算date到当前时间之间相差多少秒
     */
    public static int secondBetween(DateTime date) {
        int second = Seconds.secondsBetween(date, DateTime.now()).getSeconds();
        return second;
    }

    /**
     * @param date
     * @return 计算date到当前时间之间相差多少秒
     */
    public static int secondBetween(DateTime start, DateTime end) {
        int second = Seconds.secondsBetween(start, end).getSeconds();
        return second;
    }

    /**
     * @param date
     * @return 计算date到当前时间之间相差多少秒
     */
    public static int minuteBetween(DateTime start, DateTime end) {
        int second = Minutes.minutesBetween(start, end).getMinutes();
        return second;
    }

    /**
     * @param date
     * @return 计算date到当前时间之间相差多少秒
     */
    public static int secondBetween(Date start, Date end) {
        int second = Seconds.secondsBetween(new DateTime(start), new DateTime(end)).getSeconds();
        return second;
    }

    /**
     * @param date
     * @return 计算date到当前时间之间相差多少小时
     */
    public static int hoursBetween(Date date) {
        int hours = Hours.hoursBetween(new DateTime(date), DateTime.now()).getHours();
        return hours;
    }

    /**
     * @param date
     * @return 计算date到当前时间之间相差多少小时
     */
    public static int hoursBetween(DateTime date) {
        int hours = Hours.hoursBetween(date, DateTime.now()).getHours();
        return hours;
    }

    public static DateTime addSecond(int second) {
        return DateTime.now().plusSeconds(second);
    }

    public static Date addHours(Date date, int hours) {
        DateTime dt = new DateTime(date).plusHours(hours);
        return dt.toDate();
    }

    public static Date addHours(int hours) {
        return DateTime.now().plusHours(hours).toDate();
    }

    public static Date addDay(Date date, int day) {
        DateTime dt = new DateTime(date).plusDays(day);
        return dt.toDate();
    }

    public static Date addDay(int day) {
        return DateTime.now().plusDays(day).toDate();
    }

    /**
     * @param date
     * @return 判断date是否在当前时间之前
     */
    public static boolean isBefore(Date date) {
        return new DateTime(date).isBefore(DateTime.now());
    }

    /**
     * @param date
     * @return 判断date是否在当前时间之前
     */
    public static boolean isBefore(DateTime date) {
        return date.isBefore(DateTime.now());
    }

    /**
     * @param date
     * @return 判断date是否在当前时间之后
     */
    public static boolean isAfter(Date date) {
        return new DateTime(date).isAfter(DateTime.now());
    }

    /**
     * @param date
     * @return 判断date是否在当前时间之后
     */
    public static boolean isAfter(DateTime date) {
        return new DateTime(date).isAfter(DateTime.now());
    }

    /**
     * 当天0点(ms). The time of midnight at the start of the day, '00:00'.
     */
    public static long midnight() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long toMillis(LocalDateTime ldt) {
        return ldt.toInstant(DEFAULT_ZONE_OFFSET).toEpochMilli();
    }

    public static String duration(Delayed sf) {
        return duration(sf.getDelay(MILLISECONDS));
    }

    public static String duration(long millis) {
        StringBuilder sb = new StringBuilder();
        long sec = SECONDS.convert(millis, MILLISECONDS);
        long minutes = MINUTES.convert(sec, SECONDS);
        long hour = HOURS.convert(minutes, MINUTES);
        long day = DAYS.convert(hour, HOURS);
        if (day > 0) {
            sb.append(day).append(" day ");
            hour = hour - day * 24;
            minutes = minutes - day * 24 * 60;
            sec = sec - day * 24 * 60 * 60;
        }
        if (hour > 0) {
            sb.append(hour).append(" hour ");
            minutes = minutes - hour * 60;
            sec = sec - hour * 60 * 60;
        }

        if (minutes > 0) {
            sb.append(minutes).append(" min ");
            sec = sec - minutes * 60;
        }

        sb.append(sec).append(" sec");
        return sb.toString();
    }

    /**
     * 今天的结束时间=23:59:59
     *
     * @return
     */
    public static DateTime getTodayEndTime() {
        return DateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(0);
    }

    /** 相差的时间(秒) */
    public static int difTime(Date startTime) {
        return (int) ((System.currentTimeMillis() - startTime.getTime()) / SECOND);
    }

    /** 相差的时间(小时) */
    public static int difTimeHour(long timeMillis) {
        return (int) (Math.ceil(System.currentTimeMillis() - timeMillis) / HOUR);
    }

    /** 相差的时间(毫秒) */
    public static long difTimeMill(long timeMillis) {
        return System.currentTimeMillis() - timeMillis;
    }
    
    /**
     * 判断当时日期是周几
     */
    public static int getCurrWeekDay() {
        return getWeekDay(new Date(System.currentTimeMillis()));
    }

    /**
     * 判断日期是星期几
     */
    public static int getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int nDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (nDay == 0) {
            nDay = 7;
        }
        return nDay;
    }
    
    /**
     *  离某时间点, 还剩余时间(毫秒)
     * @param time
     * @return
     */
    public long getExpeirTimeMills(long time) {
        long midnight = DateTimeUtil.midnight();
        long curr = System.currentTimeMillis();
        return midnight + time - curr;
    }
}
