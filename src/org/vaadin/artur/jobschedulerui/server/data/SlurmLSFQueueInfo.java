package org.vaadin.artur.jobschedulerui.server.data;

import java.util.HashSet;
import java.util.List;

public class SlurmLSFQueueInfo extends QueueInfo {

	public void parseGroupData(String group, List<String> groupData) {
		if (group.equals("MAXIMUM LIMITS")) {
			for (int i = 0; i < groupData.size(); i++) {
				if (groupData.get(i).trim().equals("RUNLIMIT")) {
					String value = groupData.get(i + 1);
					if (value.contains(" of ")) {
						value = value.split(" of ")[0];
					}

					int timeInSeconds = timeToSeconds(value);
					setMaximumRunTimeInSeconds(timeInSeconds);
				} else if (groupData.get(i).trim().equals("PROCLIMIT")) {
					String value = groupData.get(i + 1).trim();
					setMaximumCores(Integer.parseInt(value));
				}
			}

		} else if (group.equals("USERS")) {
			if (groupData.size() == 1 && groupData.get(0).trim().equals("all")) {
				setUsersWithAccess(null);
			} else {
				HashSet<String> usersWithAccess = new HashSet<String>();
				for (String user : groupData.get(0).trim().split(" ")) {
					usersWithAccess.add(user.trim());
				}
				setUsersWithAccess(usersWithAccess);
			}

		} else if (group.startsWith("PRIO NICE STATUS")) {
			// PRIO NICE STATUS MAX JL/U JL/P JL/H NJOBS PEND RUN SSUSP USUSP
			// RSV
			String[] stats = groupData.get(0).trim().split("[ ]+", 13);
			setPriority(Integer.parseInt(stats[0]));
			setEnabled("Open:Active".equals(stats[2]));
			// setMaximumCores(Integer.parseInt(stats[3]));
			setJobsTotal(Integer.parseInt(stats[7]));
			setJobsRunning(Integer.parseInt(stats[9]));

		} else {
			System.out.println("Group: " + group);
			System.out.println("GroupData: " + groupData);
		}
	}

	/**
	 * converts <number> seconds/min/hours
	 * 
	 * @param value
	 * @return
	 */
	private static int timeToSeconds(String value) {
		String[] parts = value.trim().split("[ ]+");
		double number = Double.parseDouble(parts[0]);
		if ("seconds".equals(parts[1]) || "s".equals(parts[1])) {
			return (int) number;
		} else if ("min".equals(parts[1])) {
			return (int) (number * 60);
		} else if ("hours".equals(parts[1])) {
			return (int) (number * 60 * 60);
		}

		return -1;
	}
}
