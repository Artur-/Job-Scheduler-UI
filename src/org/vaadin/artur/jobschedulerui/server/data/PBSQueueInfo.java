package org.vaadin.artur.jobschedulerui.server.data;

import java.util.HashSet;

import org.vaadin.artur.jobschedulerui.ui.util.DateUtil;

public class PBSQueueInfo extends QueueInfo {

	@Override
	public void setParameter(String name, String value) {
		value = value.trim();
		super.setParameter(name, value);
		if (name.equals("state_count")) {
			int running = Integer.parseInt(value.replaceFirst(".*Running:", "")
					.replaceFirst(" .*", ""));
			setJobsRunning(running);
		} else if (name.equals("acl_user_enable")) {
			String userString = value;
			if (userString == null || userString.isEmpty()) {
				return;
			}

			String[] users = userString.split(",");
			HashSet<String> userSet = new HashSet<String>();
			for (String u : users) {
				userSet.add(u);
			}

			setUsersWithAccess(userSet);
		} else if (name.equals("resources_max.mppwidth")) {
			setMaximumCores(Integer.parseInt(value));
		} else if (name.equals("resources_max.mppwidth")) {
			setMaximumCores(Integer.parseInt(value));
		} else if (name.equals("resources_min.mppwidth")) {
			setMinimumCores(Integer.parseInt(value));
		} else if (name.equals("resources_max.mppmem")) {
			setMaximumMemory(PBSUtil.parseBytes(value));
		} else if (name.equals("resources_max.walltime")) {
			setMaximumRunTimeInSeconds(DateUtil.parseSeconds(value));
		} else if (name.equals("total_jobs")) {
			setJobsTotal(Integer.parseInt(value.trim()));
		} else if (name.equals("enabled")) {
			setEnabled(Boolean.parseBoolean(value));
		} else {
			System.out.println("Unhandled value: " + name + "=" + value);
		}
	}

}
