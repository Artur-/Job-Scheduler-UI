package org.vaadin.artur.jobschedulerui.server.data;

public class PBSUtil {
	public static Integer parseBytes(String value) {
		if (value == null) {
			return null;
		}
		try {
			int base = 1;
			if (value.contains("kb")) {
				base = 1024;
				value = value.replace("kb", "");
			} else if (value.contains("mb")) {
				base = 1024 * 1024;
				value = value.replace("mb", "");
			} else if (value.contains("gb")) {
				base = 1024 * 1024 * 1024;
				value = value.replace("gb", "");
			}
			int number = Integer.parseInt(value);

			return number * base;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

}
