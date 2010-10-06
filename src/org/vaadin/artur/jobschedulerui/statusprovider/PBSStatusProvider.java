package org.vaadin.artur.jobschedulerui.statusprovider;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.PBSQueueInfo;
import org.vaadin.artur.jobschedulerui.server.data.QueueInfo;

public class PBSStatusProvider implements StatusProvider {

	private static final String QSTAT_LIST_QUEUES = "qstat -Q -f";
	private static final String QSTAT_LIST_JOBS = "qstat -f";

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

	protected static void parseSingleQstatResult(String result,
			RowParser rowParser) {
		if (result == null || result.length() == 0) {
			return;
		}

		String[] rows = result.trim().split("[\r\n]+");

		String id = rows[0].trim();
		rowParser.setIdentifier(id);
		String continuation = null;

		for (int i = rows.length - 1; i >= 1; i--) {
			String row = rows[i];
			if (continuation != null) {
				row = row + continuation;
			}

			if (row.startsWith("        ") || row.startsWith("\t")) {
				// This is a continuation of the previous row
				continuation = row.trim();
				continue;
			} else {
				continuation = null;
			}

			String[] data = row.split("=", 2);
			if (data.length != 2) {
				System.err.println("invalid data: " + row);
				continue;
			}
			String param = data[0].trim();
			String value = data[1].trim();

			rowParser.handleParameter(param, value);
		}

	}

	@Override
	public List<QueueInfo> parseQueues(String commandResult) {
		List<QueueInfo> queueInfos = new ArrayList<QueueInfo>();

		// System.err.println("Queues: " + commandResult);
		String[] queues = commandResult.split("Queue:");

		for (String queue : queues) {
			final PBSQueueInfo queueInfo = new PBSQueueInfo();

			parseSingleQstatResult(queue, new RowParser() {

				@Override
				public void setIdentifier(String identifier) {
					queueInfo.setQueueName(identifier);
				}

				public void handleParameter(String param, String value) {
					queueInfo.setParameter(param, value);
				}

			});
			if (queueInfo.getQueueName() != null) {
				queueInfos.add(queueInfo);
			}
		}

		return queueInfos;
	}

	@Override
	public List<JobInfo> parseJobs(String result) {
		List<JobInfo> jobInfos = new ArrayList<JobInfo>();

		// System.err.println("Queues: " + commandResult);
		String[] queues = result.split("Job Id:");

		for (String queue : queues) {
			final PBSJobInfo jobInfo = new PBSJobInfo();

			parseSingleQstatResult(queue, new RowParser() {

				@Override
				public void setIdentifier(String identifier) {
					jobInfo.setIdentifier(identifier);
				}

				public void handleParameter(String param, String value) {
					jobInfo.setParameter(param, value);
				}

			});

			if (jobInfo.getIdentifier() != null) {
				jobInfos.add(jobInfo);
			}
		}

		return jobInfos;
	}

}
