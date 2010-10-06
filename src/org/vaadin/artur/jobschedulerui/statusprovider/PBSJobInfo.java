package org.vaadin.artur.jobschedulerui.statusprovider;

import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.JobState;
import org.vaadin.artur.jobschedulerui.server.data.PBSUtil;
import org.vaadin.artur.jobschedulerui.ui.util.DateUtil;

public class PBSJobInfo extends JobInfo {

	@Override
	protected void setParameter(String name, String value) {
		value = value.trim();
		super.setParameter(name, value);

		if (name.equals("Resource_List.mppmem")) {
			setMemoryPerCoreRequested(PBSUtil.parseBytes(value));
		} else if (name.equals("Job_Name")) {
			setName(value);
		} else if (name.equals("estimated.start_time")) {
			setEstimatedStartTime(DateUtil.parseDate(value));
		} else if (name.equals("stime")) {
			setStartTime(DateUtil.parseDate(value));
		} else if (name.equals("Resource_List.mppwidth")) {
			setCoresRequested(Integer.parseInt(value));
		} else if (name.equals("Resource_List.walltime")) {
			setSecondsReserved(DateUtil.parseSeconds(value));
		} else if (name.equals("resources_used.walltime")) {
			setSecondsUsed(DateUtil.parseSeconds(value));
		} else if (name.equals("job_state")) {
			setState(JobState.getState(value));
		} else if (name.equals("Job_Owner")) {
			setOwner(value.split("@", 2)[0]);
		} else if (name.equals("comment")) {
			setComment(value);
		} else {
			// System.out.println("Unhandled value: " + name + "=" + value);
		}
	}
}
