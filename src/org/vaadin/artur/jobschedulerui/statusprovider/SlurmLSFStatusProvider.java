package org.vaadin.artur.jobschedulerui.statusprovider;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.QueueInfo;
import org.vaadin.artur.jobschedulerui.server.data.SlurmLSFQueueInfo;

public class SlurmLSFStatusProvider implements StatusProvider {

	private static final String QSTAT_LIST_QUEUES = "bqueues -l";
	private static final String QSTAT_LIST_JOBS = "bjobs -u all -p -r -s -l";

	@Override
	public String getAvailableQueuesCommand() {
		return QSTAT_LIST_QUEUES;
	}

	@Override
	public String getListJobsCommand() {
		return QSTAT_LIST_JOBS;
	}

	protected interface RowParser {
		public void setIdentifier(String id);

		public void handleParameter(String param, String value);
	}

	protected static void parseQueue(String result, SlurmLSFQueueInfo queueInfo) {
		if (result == null || result.length() == 0 || result.equals("\n")) {
			return;
		}

		String[] rows = result.trim().split("[\r\n]+");

		String queueName = rows[0].trim();
		String comment = rows[1].trim().replaceAll("^-- ", "");
		queueInfo.setQueueName(queueName);
		queueInfo.setComment(comment);

		String group = "";

		for (int i = 2; i < rows.length; i++) {
			String row = rows[i];

			if (isGroupRow(row)) {
				group = row.trim().split(":")[0];
				List<String> groupData = new ArrayList<String>();
				if (row.contains(":")) {
					groupData.add(row.trim().split(":", 2)[1]);
				}

				while (i < (rows.length - 1) && !isGroupRow(rows[++i])) {
					groupData.add(rows[i]);
				}
				i--;
				queueInfo.parseGroupData(group, groupData);
				group = "";
				if (i == rows.length - 2) {
					break;
				}
			}
		}
	}

	protected static void parseJob(String result, SlurmLSFJobInfo jobInfo) {
		if (result == null || result.length() == 0 || result.equals("\n")) {
			return;
		}

		String[] rows = result.trim().split("[\r\n]+");

		String continueIndent = "                     ";

		List<String> realRows = new ArrayList<String>();

		String row = rows[0].trim();
		for (int i = 1; i < rows.length; i++) {
			if (rows[i].startsWith(continueIndent)) {
				row += rows[i].substring(continueIndent.length());
			} else {
				realRows.add(row);
				row = rows[i];
			}
		}

		for (int i = 0; i < realRows.size(); i++) {
			String realRow = realRows.get(i);
			// Join rows like
			// RUNLIMIT
			// 9000.0 min of lsfhost.localdomain
			// into one
			if (realRow.matches("^ [A-Z: ]*[ ]*$")) {
				i++;
				realRow = realRow + realRows.get(i);
			}
			jobInfo.handleRow(realRow);
		}

	}

	private static boolean isGroupRow(String row) {
		return (row.matches("^[A-Z][A-Z].*"));

	}

	@Override
	public List<QueueInfo> parseQueues(String commandResult) {
		List<QueueInfo> queueInfos = new ArrayList<QueueInfo>();

		// System.err.println("Queues: " + commandResult);
		String[] queues = commandResult.split("QUEUE:");

		for (String queue : queues) {
			final SlurmLSFQueueInfo queueInfo = new SlurmLSFQueueInfo();

			parseQueue(queue, queueInfo);
			if (queueInfo.getQueueName() != null) {
				queueInfos.add(queueInfo);
			}
		}

		return queueInfos;
	}

	@Override
	public List<JobInfo> parseJobs(String result) {
		List<JobInfo> jobInfos = new ArrayList<JobInfo>();

		// // System.err.println("Queues: " + commandResult);
		String[] jobs = result.split("Job <");

		for (String job : jobs) {
			if (job.trim().equals("")) {
				continue;
			}

			job = "Job <" + job;
			final SlurmLSFJobInfo jobInfo = new SlurmLSFJobInfo();

			parseJob(job, jobInfo);
			if (jobInfo.getIdentifier() != null) {
				jobInfos.add(jobInfo);
			}
		}

		return jobInfos;
	}

}
