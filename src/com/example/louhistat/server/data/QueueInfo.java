package com.example.louhistat.server.data;

import java.util.HashMap;
import java.util.Map;

public class QueueInfo {

	private String queueName;
	private Map<String, String> parameters = new HashMap<String, String>();

	public QueueInfo(String queueName) {
		this.queueName = queueName;
	}

	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	public String getQueueName() {
		return queueName;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	// Cray specifics

	public Integer getJobsRunning() {
		int running = Integer.parseInt(getParameter("state_count")
				.replaceFirst(".*Running:", "").replaceFirst(" .*", ""));
		return running;
	}

	public Boolean isRestricted() {
		return getParameter("acl_user_enable") != null;
	}

	public Boolean hasAccess(String username) {
		if (!isRestricted()) {
			return true;
		}

		String users = getParameter("acl_users");
		if (users == null) {
			return false;
		}

		// Prepend and append comma so we can match ,username,
		users = "," + users + ",";
		return users.contains("," + username + ",");
	}

	public Integer getMaximumCores() {
		return getIntegerParameter("resources_max.mppwidth");
	}

	public Integer getMinimumCores() {
		return getIntegerParameter("resources_min.mppwidth");
	}

	public Integer getMaximumMemory() {
		return getIntegerParameter("resources_max.mppmem");
	}

	private Integer getIntegerParameter(String parameter) {
		String value = getParameter(parameter);
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

	public Long getMaximumRunTimeInSeconds() {
		String value = getParameter("resources_max.walltime");
		if (value == null) {
			return -1L;
		}

		String[] parts = value.split(":");
		if (parts.length != 3) {
			System.err.println("Error parsing runtime from '" + value + "'");
		}
		return Long.parseLong(parts[0]) * 3600L + Long.parseLong(parts[1])
				* 60L + Long.parseLong(parts[2]);
	}

	public Boolean isEnabled() {
		return getBooleanParameter("enabled", false);
	}

	private boolean getBooleanParameter(String name, boolean defaultValue) {
		String value = getParameter(name);
		if (value == null) {
			return defaultValue;
		}

		return value.equalsIgnoreCase("true");
	}

}
