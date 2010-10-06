package org.vaadin.artur.jobschedulerui.ui.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	private static final Locale locale = new Locale("fi", "FI");

	// FIXME: Where do I get the locale from?
	// FIXME: Cannot be static as locale can differ between app instances
	private static final DateFormat DATE_TIME_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);

	private static final DateFormat DATE_TIME_FORMAT_QSTAT = new SimpleDateFormat(
			"E MMM d hh:mm:ss yyyy", new Locale("en", "US"));

	// Mon Sep 27 16:34:14 2010
	public static String QStatFormatDateTime(Date date) {
		return DATE_TIME_FORMAT_QSTAT.format(date);
	}

	public static String formatDateTime(Date date) {
		return DATE_TIME_FORMAT.format(date);
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(DATE_TIME_FORMAT_QSTAT.format(new Date()));
		System.out.println(DATE_TIME_FORMAT_QSTAT
				.parse("Fri Oct  1 23:42:50 2010"));
	}

	/**
	 * Parses date in format "Wed Oct  6 17:48:00 2010"
	 * 
	 * @param parameter
	 * @return
	 */
	public static Date parseDate(String dateString) {
		if (dateString == null) {
			return null;
		}

		try {
			return DATE_TIME_FORMAT_QSTAT.parse(dateString);
		} catch (ParseException e) {
			System.err.println("Could not parse date: '" + dateString + "'");
			return null;
		}

	}

	/**
	 * Parses time in format "hh:mm:ss" into seconds.
	 * 
	 * @param value
	 * @return
	 */
	public static Integer parseSeconds(String value) {
		if (value == null) {
			return null;
		}

		String[] parts = value.split(":");
		if (parts.length != 3) {
			System.err.println("Error parsing seconds from '" + value + "'");
			return null;
		}
		return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1])
				* 60 + Integer.parseInt(parts[2]);

	}

	public static String secondsToString(int seconds) {
		int h = (seconds / 3600);
		seconds -= h * 3600;
		int m = ((seconds) / 60);
		seconds -= m * 60;
		int s = (seconds);

		String result = h + ":";
		if (m < 10) {
			result += "0" + m;
		} else {
			result += m;
		}
		result += ":";

		if (s < 10) {
			result += "0" + s;
		} else {
			result += s;
		}
		return result;
	}
}
