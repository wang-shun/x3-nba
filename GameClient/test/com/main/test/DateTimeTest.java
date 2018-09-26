package com.main.test;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

public class DateTimeTest {

	public static void main(String[] args) {
		DateTime d1 = DateTime.now().minusDays(32);
		DateTime d2 = DateTime.now();
		//int day = Days.daysBetween(d1, d2).getDays();
		int day = Months.monthsBetween(d1, d2).getMonths();
		System.err.println(day);
	}
	
}
