package org.vaadin.artur.jobschedulerui.statusprovider;

import java.util.Date;

import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.JobState;
import org.vaadin.artur.jobschedulerui.ui.util.DateUtil;

public class SlurmLSFJobInfo extends JobInfo {

	public void handleJobAttrib(String name, String value) {
		if ("Job".equals(name)) {
			setIdentifier(value);
		} else if ("Job Name".equals(name)) {
			setName(value);
		} else if ("User".equals(name)) {
			setOwner(value);
		} else if ("Status".equals(name)) {
			setState(getState(value));
		} else if ("Queue".equals(value)) {
			setQueue(value);
		} else {
			System.out.println(name + ": " + value);
		}
	}

	private JobState getState(String value) {
		if ("PEND".equals(value)) {
			return JobState.QUEUED;
		} else if ("RUN".equals(value)) {
			return JobState.RUNNING;
		} else if ("PSUSP".equals(value)) {
			return JobState.ON_HOLD;
		}

		return JobState.UNKNOWN;
	}

	public void handleRow(String row) {
		if (row.startsWith("Job <")) {
			String[] jobAttribs = row.split(",");
			for (String jobAttrib : jobAttribs) {
				String[] parts = jobAttrib.split("<", 2);
				if (parts.length != 2) {
					System.out.println("Skipped: " + jobAttrib);
					continue;
				}
				String name = parts[0].trim();
				// Skip >
				String value = parts[1].substring(0, parts[1].length() - 1);

				handleJobAttrib(name, value);
			}
		} else if (row.contains("slurm_id=")) {
			// Mon Oct 25 18:21:06: slurm_id=6831577;ncpus=4;slurm_alloc=c195;
			row = row.substring(row.indexOf("slurm_id"));
			// slurm_id=6831577;ncpus=4;slurm_alloc=c195;
			String[] attribs = row.split(";");
			for (String attrib : attribs) {
				String[] parts = attrib.split("=");
				if ("ncpus".equals(parts[0])) {
					setCoresRequested(Integer.parseInt(parts[1]));
				}
			}
		} else if (row.contains("PENDING REASONS:")) {
			row = row.substring(row.indexOf(": ") + 2);
			setComment(row);
		} else if (row.contains("CPULIMIT")) {
		} else if (row.contains("MEMLIMIT")) {
		} else if (row.contains("RUNLIMIT")) {
			// RUNLIMIT 30240.0 min of lsfhost.localdomain
			row = row.substring("RUNLIMIT ".length()).trim();
			row = row.substring(0, row.indexOf(' '));
			double minutes = Double.parseDouble(row);
			setSecondsReserved((int) (minutes * 60));

		} else if (row.contains("Estimated job start time")) {
			setEstimatedStartTime(getTimestamp(row));

		} else if (row.contains("Submitted from host")) {
			// Tue Sep 7 13:10:41: Submitted from host <c552>, CWD
			// </wrk/asn/SVN/ASCOT/branches/asn/IAEA/productionrun/ntm_both_andiff>,
			// Output File <ascot.out.%J>, Error File <ascot.err.%J>, 128
			// Processors Requested, Requested Resources <type=any>;
			setSubmitTime(getTimestamp(row));
		} else if (row.toLowerCase().contains("started on")) {
			// Wed Oct 13 13:17:56: Started on <lsfhost.localdomain>, Execution
			// Home </home/csc/chipster>, Execution CWD
			// </home/csc/chipster/development/chipster>;
			Date startTime = getTimestamp(row);
			setStartTime(startTime);
			long running = new Date().getTime() - startTime.getTime();
			setSecondsUsed((int) (running / 1000));

		} else {
			System.out.println("Unhandled row: " + row);
		}

	}

	private Date getTimestamp(String row) {
		String timestamp = row.substring(0, row.indexOf(": "));
		return DateUtil.parseDate(timestamp + " "
				+ (new Date().getYear() + 1900));
	}

}
