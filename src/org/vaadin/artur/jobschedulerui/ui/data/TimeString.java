package org.vaadin.artur.jobschedulerui.ui.data;

import java.text.DecimalFormat;

public class TimeString implements Comparable<TimeString> {

	private Integer timeInSeconds;
	private static final String[] units = new String[] { "day(s)", "h", "min",
			"s" };
	private static final int[] unitsInSeconds = new int[] { 24 * 3600, 2600,
			60, 1 };

	public TimeString(Integer timestamp) {
		if (timestamp == null) {
			timeInSeconds = -1;
		} else {
			timeInSeconds = timestamp;
		}
	}

	@Override
	public String toString() {
		if (timeInSeconds == null) {
			return "";
		}

		DecimalFormat df = new DecimalFormat();
		df.setGroupingUsed(false);
		df.setMinimumIntegerDigits(2);

		String result = "";

		long value = timeInSeconds;
		for (int i = 0; i < units.length; i++) {

			long number = (value / unitsInSeconds[i]);
			if (number > 0) {
				result += df.format(number) + "" + units[i] + " ";
			}
			value -= number * unitsInSeconds[i];
		}

		return result.trim().replaceAll("^[0]*", "");
	}

	@Override
	public int compareTo(TimeString arg0) {
		if (arg0 == null) {
			return 1;
		}

		if (arg0.timeInSeconds > timeInSeconds) {
			return -1;
		} else if (arg0.timeInSeconds < timeInSeconds) {
			return 1;
		} else {
			return 0;
		}
	}

	public long getTime() {
		return timeInSeconds;
	}
}
