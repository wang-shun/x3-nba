package com.ftkj.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilDateTime{

	static long SECOND_SEC_NUMBER = 1L;
	static long MINUTE_SEC_NUMBER = 60L;
	static long HOUR_SEC_NUMBER = 3600L;
	static long DAY_SEC_NUMBER = HOUR_SEC_NUMBER * 24L;
	static long MONTH_SEC_NUMBER = DAY_SEC_NUMBER * 30L;
	static long YEAR_SEC_NUMBER = DAY_SEC_NUMBER * 365L;	
	public static String DEFAULTFORMAT = "yyyy-MM-dd HH:mm:ss";
	public static String SIMPLEFORMATSTRING = "yyyy-MM-dd HH:mm";	
	public static String MM_DD = "MM-dd";
	public static String YYYYMMDD = "yyyyMMdd";
	public static String YYYYMM = "yyyyMM";
	public static String MM = "MM";
	public static String YYYY_MM_DD = "yyyy-MM-dd";
	public static String YY_MM_DD = "yy-MM-dd";
	public static String SORTSTRING = "MM-dd HH:mm";
	public static String HH_MM = "HH:mm";
	public static String HH_MM_SS="HH:mm:ss";

	/**
	 * 把字串格式化成日期
	 * @param strDate
	 * @param pattern
	 * @return
	 */
	public static Date toSqlDateByPattern(String strDate, String pattern){
		SimpleDateFormat formatter;
		try{
			formatter = new SimpleDateFormat(pattern);
			return new Date(formatter.parse(strDate).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date(0L);
	}

	/**
     * 把字串格式化成日期
     * @param strDate
     * @param pattern
     * @return
     */
	public static Date toDateTimeByPattern(String strDate, String pattern){
		SimpleDateFormat formatter;
		try{
			formatter = new SimpleDateFormat(pattern);
			return new Date(formatter.parse(strDate).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date(0L);
	}

	/**
	 * 把字串格式化成日期
	 * @param dDate
	 * @return
	 */
	public static Date toSqlDataTime(String dDate){
		String dateFormat = "yyyy-MM-dd";
		if ((dDate.length() > 10) && (dDate.length() <= 16))
			dateFormat = "yyyy-MM-dd HH:mm";
		else if ((dDate.length() > 16) && (dDate.length() <= 19))
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		else if (dDate.length() > 19)
			dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

		Date d1 = toSqlDateByPattern(dDate, dateFormat);
		return d1;
	}

	/**
	 * 根据字符串长度格式化日期
	 * @param dDate
	 * @return
	 */
	public static Date toDataTime(String dDate){
		String dateFormat = "yyyy-MM-dd";
		if (dDate == null) return null;
		
		if ((dDate.length() > 10) && (dDate.length() <= 16))
			dateFormat = "yyyy-MM-dd HH:mm";
		else if ((dDate.length() > 16) && (dDate.length() <= 19))
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		else if (dDate.length() > 19)
			dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

		Date d1 = toDateTimeByPattern(dDate, dateFormat);
		return d1;
	}
	
	/**
	 * 把日期转换成字串
	 * @param d
	 * @param pattern
	 * @return
	 */
	public static String toSqlDateString(Date d, String pattern){
		String rs = "";
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			rs = formatter.format(d);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ((isFirstStartDate(rs)) ? "" : rs);
	}

	private static boolean isFirstStartDate(String strDate){
		return ((strDate.compareTo("1970-01-01") == 0) || (strDate.compareTo("1899-12-30") == 0) || (strDate.compareTo("1969-12-31") == 0));
	}

	/**
	 * 得到当前日期时间
	 * @return
	 */
	public static Timestamp nowTimestamp(){
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 得到当前日期
	 * @param stamp
	 * @return
	 */
	public static Timestamp getDayStart(Timestamp stamp) {
		return getDayStart(stamp, 0);
	}

	/**
	 * 得到当前日期的相隔天数
	 * @param stamp
	 * @param daysLater
	 * @return
	 */
	public static Timestamp getDayStart(Timestamp stamp, int daysLater){
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(new Date(stamp.getTime()));
		tempCal.set(tempCal.get(1), tempCal.get(2), tempCal.get(5), 0, 0, 0);
		if(daysLater != 0)
		    tempCal.add(5, daysLater);
		return new Timestamp(tempCal.getTime().getTime());
	}
	
	public static Date getDayStart(Date date){
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(date);
		tempCal.set(tempCal.get(1), tempCal.get(2), tempCal.get(5), 0, 0, 0);
		return tempCal.getTime();
	}

	/**
	 * 得到明天的日期
	 * @param stamp
	 * @return
	 */
	public static Timestamp getNextDayStart(Timestamp stamp) {
		return getDayStart(stamp, 1);
	}

	/**
	 * 得到当天的日期和结束时间
	 * @param stamp
	 * @return
	 */
	public static Timestamp getDayEnd(Timestamp stamp) {
		return getDayEnd(stamp, 0);
	}

	/**
	 * 得到当天相隔几天的日期和结束时间
	 * @param stamp
	 * @param daysLater
	 * @return
	 */
	public static Timestamp getDayEnd(Timestamp stamp, int daysLater){
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(new Date(stamp.getTime()));
		tempCal.set(tempCal.get(1), tempCal.get(2), tempCal.get(5), 23, 59, 59);
		tempCal.add(5, daysLater);
		return new Timestamp(tempCal.getTime().getTime());
	}
	/**
	 * 得到当天结束时间
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Date date, int daysLater) {
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(date);
		tempCal.set(tempCal.get(1), tempCal.get(2), tempCal.get(5), 23, 59, 59);
		tempCal.add(5, daysLater);
		return tempCal.getTime();
	}

	/**
	 * 字符串格式化成日期
	 * @param date
	 * @return
	 */
	public static Date toSqlDate(String date){
		Date newDate = toDate(date, "00:00:00");
		if (newDate != null)
			return new Date(newDate.getTime());

		return null;
	}

	/**
	 * 根据年、月、日得到日期
	 * @param monthStr 字符串
	 * @param dayStr 字符串
	 * @param yearStr 字符串
	 * @return
	 */
	public static Date toSqlDate(String monthStr, String dayStr, String yearStr){
		Date newDate = toDate(monthStr, dayStr, yearStr, "0", "0", "0");
		if (newDate != null)
			return new Date(newDate.getTime());

		return null;
	}

	/**
	 * 根据年、月、日得到日期
	 * @param month 整形
	 * @param day 整形
	 * @param year 整形
	 * @return
	 */
	public static Date toSqlDate(int month, int day, int year){
		Date newDate = toDate(month, day, year, 0, 0, 0);
		if (newDate != null)
			return new Date(newDate.getTime());

		return null;
	}

	/**
	 * 字符串格式化成时间
	 * @param time 格式"00:00:00"
	 * @return
	 */
	public static Time toSqlTime(String time){
		Date newDate = toDate("1/1/1970", time);
		if (newDate != null)
			return new Time(newDate.getTime());

		return null;
	}

	/**
	 * 字符串格式化成时间
	 * @param hourStr 字符串  - 小时 
	 * @param minuteStr 字符串  - 分
	 * @param secondStr 字符串  - 秒
	 * @return
	 */
	public static Time toSqlTime(String hourStr, String minuteStr, String secondStr){
		Date newDate = toDate("0", "0", "0", hourStr, minuteStr, secondStr);
		if (newDate != null)
			return new Time(newDate.getTime());

		return null;
	}

	/**
	 * 整形格式化成时间
	 * @param hour 整形  - 小时
	 * @param minute 整形 - 分
	 * @param second 整形 - 秒
	 * @return
	 */
	public static Time toSqlTime(int hour, int minute, int second){
		Date newDate = toDate(0, 0, 0, hour, minute, second);
		if (newDate != null)
			return new Time(newDate.getTime());

		return null;
	}

	/**
	 * 字符串格式化成日期时间戳
	 * @param dateTime 格式 "MM/dd/yyyy HH:mm:ss"
	 * @return
	 */
	public static Timestamp toTimestamp(String dateTime){
		Date newDate = toDate(dateTime);
		if (newDate != null)
			return new Timestamp(newDate.getTime());

		return null;
	}

	/**
	 * 字符串格式化成日期时间戳
	 * @param date 格式 "MM/dd/yyyy"
	 * @param time 格式 "HH:mm:ss"
	 * @return
	 */
	public static Timestamp toTimestamp(String date, String time){
		Date newDate = toDate(date, time);
		if (newDate != null)
			return new Timestamp(newDate.getTime());

		return null;
	}

	/**
	 * 字符串格式化成日期时间戳
	 * @param monthStr 字符串 - 月
	 * @param dayStr 字符串 - 日
	 * @param yearStr 字符串 - 年
	 * @param hourStr 字符串 - 小时
	 * @param minuteStr 字符串 - 分
	 * @param secondStr 字符串 - 秒
	 * @return
	 */
	public static Timestamp toTimestamp(String monthStr, String dayStr, String yearStr, String hourStr, String minuteStr, String secondStr){
		Date newDate = toDate(monthStr, dayStr, yearStr, hourStr, minuteStr, secondStr);
		if (newDate != null)
			return new Timestamp(newDate.getTime());

		return null;
	}
	
	/**
     * 字符串格式化成日期时间戳
     * @param monthStr 整形 - 月
     * @param dayStr 整形 - 日
     * @param yearStr 整形  - 年
     * @param hourStr 整形  - 小时
     * @param minuteStr 整形 - 分
     * @param secondStr 整形 - 秒
     * @return
     */
	public static Timestamp toTimestamp(int month, int day, int year, int hour, int minute, int second){
		Date newDate = toDate(month, day, year, hour, minute, second);
		if (newDate != null)
			return new Timestamp(newDate.getTime());

		return null;
	}

	/**
	 * 字符串格式化日期时间
	 * @param dateTime MM/dd/yyyy hh:mm:ss
	 * @return
	 */
	public static Date toDate(String dateTime){
		String date = dateTime.substring(0, dateTime.indexOf(" "));
		String time = dateTime.substring(dateTime.indexOf(" ") + 1);
		return toDate(date, time);
	}

	/**
	 * 字符串格式化日期时间
	 * @param date 格式 "MM/dd/yyyy"
	 * @param time 格式 "HH:mm:ss"
	 * @return
	 */
	public static Date toDate(String date, String time){
		String minute;
		String second;
		if ((date == null) || (time == null)) {
			return null;
		}

		int dateSlash1 = date.indexOf("/");
		int dateSlash2 = date.lastIndexOf("/");

		if ((dateSlash1 <= 0) || (dateSlash1 == dateSlash2))
			return null;
		int timeColon1 = time.indexOf(":");
		int timeColon2 = time.lastIndexOf(":");

		if (timeColon1 <= 0)
			return null;
		String month = date.substring(0, dateSlash1);
		String day = date.substring(dateSlash1 + 1, dateSlash2);
		String year = date.substring(dateSlash2 + 1);
		String hour = time.substring(0, timeColon1);

		if (timeColon1 == timeColon2) {
			minute = time.substring(timeColon1 + 1);
			second = "0";
		} else {
			minute = time.substring(timeColon1 + 1, timeColon2);
			second = time.substring(timeColon2 + 1);
		}

		return toDate(month, day, year, hour, minute, second);
	}

	/**
	 * 字符串格式成日期时间
	 * @param monthStr
	 * @param dayStr
	 * @param yearStr
	 * @param hourStr
	 * @param minuteStr
	 * @param secondStr
	 * @return
	 */
	public static Date toDate(String monthStr, String dayStr, String yearStr, String hourStr, String minuteStr, String secondStr){
		int month;
		int day;
		int year;
		int hour;
		int minute;
		int second;
		try{
			month = Integer.parseInt(monthStr);
			day = Integer.parseInt(dayStr);
			year = Integer.parseInt(yearStr);
			hour = Integer.parseInt(hourStr);
			minute = Integer.parseInt(minuteStr);
			second = Integer.parseInt(secondStr);
		} catch (Exception e) {
			return null;
		}
		return toDate(month, day, year, hour, minute, second);
	}

	/**
	 * 字符串格式化日期时间
	 * @param month
	 * @param day
	 * @param year
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static Date toDate(int month, int day, int year, int hour, int minute, int second){
		Calendar calendar = Calendar.getInstance();
		try{
			calendar.set(year, month - 1, day, hour, minute, second);
		} catch (Exception e) {
			return null;
		}
		return new Date(calendar.getTime().getTime());
	}
	
    /**
     * 日期格式化成日期字符串
     * @param date
     * @return "MM/dd/yyyy"
     */
	public static String toDateString(Date date){
		String monthStr;
		String dayStr;
		if (date == null)
			return "";
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		int month = calendar.get(2) + 1;
		int day = calendar.get(5);
		int year = calendar.get(1);

		if (month < 10)
			monthStr = "0" + month;
		else
			monthStr = "" + month;

		if (day < 10)
			dayStr = "0" + day;
		else
			dayStr = "" + day;

		String yearStr = "" + year;
		return monthStr + "/" + dayStr + "/" + yearStr;
	}

	/**
	 * 日期格式化成字符串
	 * @param d
	 * @param pattern 日期正则表达式
	 * @return
	 */
	public static String toDateString(Date d, String pattern){
		String rs = "";
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			rs = formatter.format(d);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ((isFirstStartDate(rs)) ? "" : rs);
	}
	
	/**
	 * 日期格式化成时间字符串
	 * @param date
	 * @return
	 */
	public static String toTimeString(Date date){
		if (date == null)
			return "";
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);
		return toTimeString(calendar.get(11), calendar.get(12), calendar.get(13));
	}

	/**
	 * 参数格式化成时间字符串
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static String toTimeString(int hour, int minute, int second){
		String hourStr;
		String minuteStr;
		String secondStr;
		if (hour < 10)
			hourStr = "0" + hour;
		else
			hourStr = "" + hour;

		if (minute < 10)
			minuteStr = "0" + minute;
		else
			minuteStr = "" + minute;

		if (second < 10)
			secondStr = "0" + second;
		else
			secondStr = "" + second;

		if (second == 0)
			return hourStr + ":" + minuteStr;

		return hourStr + ":" + minuteStr + ":" + secondStr;
	}

	/**
	 * 日期格式化成时间日期字符串
	 * @param date
	 * @return
	 */
	public static String toDateTimeString(Date date){
		if (date == null)
			return "";
		String dateString = toDateString(date);
		String timeString = toTimeString(date);

		if ((dateString != null) && (timeString != null))
			return dateString + " " + timeString;

		return "";
	}

	/**
	 * 得到当月的第一天
	 * @return
	 */
	public static Timestamp monthBegin(){
		Calendar mth = Calendar.getInstance();
		mth.set(5, 1);
		mth.set(11, 0);
		mth.set(12, 0);
		mth.set(13, 0);
		mth.set(9, 0);
		return new Timestamp(mth.getTime().getTime());
	}

	/**
	 * 得到两日前相差的秒数量
	 * @param maxTime
	 * @param minTime
	 * @return
	 */
	public static int reckonDifferenceSecond(Date maxTime, Date minTime){
		int fatSecond = 0;
		long lngMaxTime = (maxTime == null) ? 0L : maxTime.getTime();
		long lngMinTime = (minTime == null) ? 0L : minTime.getTime();
		float lngDifference = (float)(lngMaxTime - lngMinTime);
		lngDifference = Math.abs(lngDifference);
		fatSecond = (int) (lngDifference / (SECOND_SEC_NUMBER * 1000L));
		return fatSecond;
	}

	/**
     * 得到两日前相差的分钟数量
     * @param maxTime
     * @param minTime
     * @return
     */
	public static int reckonDifferenceMinute(Date maxTime, Date minTime){
		int fatSecond = 0;
		long lngMaxTime = (maxTime == null) ? 0L : maxTime.getTime();
		long lngMinTime = (minTime == null) ? 0L : minTime.getTime();
		float lngDifference = (float)(lngMaxTime - lngMinTime);
		lngDifference = Math.abs(lngDifference);
		fatSecond = (int) (lngDifference / (MINUTE_SEC_NUMBER * 1000L));
		return fatSecond;
	}

	/**
     * 得到两日前相差的小时数量
     * @param maxTime
     * @param minTime
     * @return
     */
	public static float reckonDifferenceHour(Date maxTime, Date minTime){
		float fatHour = 0F;
		long lngMaxTime = (maxTime == null) ? 0L : maxTime.getTime();
		long lngMinTime = (minTime == null) ? 0L : minTime.getTime();
		float lngDifference = (float)(lngMaxTime - lngMinTime);
		lngDifference = Math.abs(lngDifference);
		fatHour = lngDifference / (float)(HOUR_SEC_NUMBER * 1000L);
		return fatHour;
	}
	
	/**
     * 得到两日前相差的天数
     * @param maxTime
     * @param minTime
     * @return
     */
	public static float reckonDifferenceDay(Date maxTime, Date minTime){
		float fatDay = 0F;
		long lngMaxTime = (maxTime == null) ? 0L : maxTime.getTime();
		long lngMinTime = (minTime == null) ? 0L : minTime.getTime();
		float lngDifference = (float)(lngMaxTime - lngMinTime);
		lngDifference = Math.abs(lngDifference);
		fatDay = lngDifference / (float)(DAY_SEC_NUMBER * 1000L);
		return fatDay;
	}

	/**
     * 当前日期格式化成字符串
     * @param maxTime
     * @param minTime
     * @return
     */
	public static String getCurrDateStr(String format) {
		String result = "";
		if (format == null) {
			return result;
		}
		java.util.Date d = new java.util.Date();
		DateFormat df = new SimpleDateFormat(format);
		result = df.format(d);
		return result;
	}

	/**
	 * 日期增加天数
	 * @param sDate
	 * @param addDay
	 * @return
	 */
	public static Date getNextDateAddDay(Date sDate, int addDay){
		Long lngTime = Long.valueOf(sDate.getTime());
		Long addDayMSec = Long.valueOf(addDay * DAY_SEC_NUMBER * 1000L);
		return new Date(lngTime.longValue() + addDayMSec.longValue());
	}

	/**
     * 日期增加小时数
     * @param sDate
     * @param addDay
     * @return
     */
	public static Date getNextDateAddHour(Date sDate, int addHour){
		Long lngTime = Long.valueOf(sDate.getTime());
		Long addHourMSec = Long.valueOf(addHour * HOUR_SEC_NUMBER * 1000L);
		return new Date(lngTime.longValue() + addHourMSec.longValue());
	}

	/**
     * 日期增加分钟数
     * @param sDate
     * @param addDay
     * @return
     */
	public static Date getNextDateAddMin(Date sDate, int addMin){
		Long lngTime = Long.valueOf(sDate.getTime());
		Long addMinMSec = Long.valueOf(addMin * MINUTE_SEC_NUMBER * 1000L);
		return new Date(lngTime.longValue() + addMinMSec.longValue());
	}
	
	/**
     * 日期增加秒数
     * @param sDate
     * @param addDay
     * @return
     */
	public static Date getNextDateAddSecond(Date sDate, int addSecond){
		Long lngTime = Long.valueOf(sDate.getTime());
		Long addSecondMSec = Long.valueOf(addSecond * SECOND_SEC_NUMBER * 1000L);
		return new Date(lngTime.longValue() + addSecondMSec.longValue());
	}

	/**
     * 得到日期当年的第几周
     * @param sDate
     * @param addDay
     * @return
     */
	public static int getWeek(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}
	/**
	 * 得到年周
	 * @param date 
	 * @param day 日期移动天数
	 * @return 199701 或者 199730
	 */
	public static int getYearWeek(Date date,int day){
	    date = getNextDateAddDay(date, day);
	    Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        if(month == 1 && week > 5)
            year = year -1;
        if(month == 12 && week == 1)
            year = year + 1;
        return year*100 + week;
	}
	/**
	 * 得到日期是周的第几天
	 * @param date
	 * @param day 日期移动天数
	 * @return
	 */
	public static int getDayOfWeek(Date date,int day){
	    date = getNextDateAddDay(date, day);
	    Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
	}
	/**
	 * 得到当前日期时间 
	 * @return
	 */
	public static Date getCurrDateTime(){
	    Calendar cal = Calendar.getInstance();
	    return cal.getTime();
	}
	/**
	 * 比较日期是否同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean compareDate(Date maxDate,Date minDate){
	    if(maxDate == null  || minDate == null)
	        return false;
	    String max = UtilDateTime.toDateString(maxDate, UtilDateTime.YYYY_MM_DD);
	    String min = UtilDateTime.toDateString(minDate, UtilDateTime.YYYY_MM_DD);
	    return max.equals(min);
	}
	
	
	
	public static void main(String[] args){	
	   //System.out.println(UtilDateTime.toDateTimeString(UtilDateTime.getNextDateAddDay(UtilDateTime.getCurrDateTime(), -2)));
	   //int day = UtilDateTime.getDayOfWeek(UtilDateTime.getNextDateAddDay(UtilDateTime.getCurrDateTime(), -2),-1);
	   //System.out.println(day);
	    /*for(int i = 1997; i < 2050; i++){
	        int week = UtilDateTime.getYearWeek(UtilDateTime.toDate("01/01/" + i + " 00:00:00"));
	        System.out.println(week);  
	    }*/
		
		//System.out.println(UtilDateTime.toDateString(getDayEnd(new Date(), 1), UtilDateTime.DEFAULTFORMAT));;
//		System.out.println(1+(int)UtilDateTime.reckonDifferenceDay(getNextDateAddDay(new Date(), 2), new Date()));
		
		
//		int player_price = 911*95/100;
		
//		System.out.println(player_price+"/"+(911*95/100.0f));
		
		System.out.println(UtilDateTime.getYearWeek(new Date(), -3));
	}
}
