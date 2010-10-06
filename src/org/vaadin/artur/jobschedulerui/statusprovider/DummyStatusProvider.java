package org.vaadin.artur.jobschedulerui.statusprovider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.JobState;
import org.vaadin.artur.jobschedulerui.server.data.QueueInfo;
import org.vaadin.artur.jobschedulerui.ui.util.DateUtil;

public class DummyStatusProvider extends PBSStatusProvider {

	private static final String DUMMY_QUEUE_DATA_FILE = "dummy.queue.data";
	private static final String DUMMY_JOB_DATA_TEMPLATE_FILE = "dummy.job.data.template";

	@Override
	public String getAvailableQueuesCommand() {
		return null;
	}

	@Override
	public String getListJobsCommand() {
		return null;
	}

	@Override
	public List<QueueInfo> parseQueues(String commandResult) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		String basename = getClass().getName().substring(0,
				getClass().getName().lastIndexOf('.'));
		basename = basename.replace('.', File.separatorChar);
		InputStream stream = getClass().getClassLoader().getResourceAsStream(
				basename + File.separator + DUMMY_QUEUE_DATA_FILE);
		try {
			return super.parseQueues(IOUtils.toString(stream));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<QueueInfo>();
	}

	@Override
	public List<JobInfo> parseJobs(String result) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		Random r = new Random(System.currentTimeMillis());

		String basename = getClass().getName().substring(0,
				getClass().getName().lastIndexOf('.'));
		basename = basename.replace('.', File.separatorChar);
		InputStream stream = getClass().getClassLoader().getResourceAsStream(
				basename + File.separator + DUMMY_JOB_DATA_TEMPLATE_FILE);
		try {
			String template = IOUtils.toString(stream);

			int numberOfJobs = r.nextInt(500) + 1;
			StringBuilder jobs = new StringBuilder(numberOfJobs
					* (template.length() + 100));
			long lowerDate = new Date().getTime() - 1000 * 3600 * 24 * 10; // 10
																			// days
																			// back

			for (int i = 0; i < numberOfJobs; i++) {
				HashMap<String, String> values = new HashMap<String, String>();
				values.put("id", (1001 + i) + "");
				values.put("name", "My random job " + r.nextInt(50));
				values.put("owner", "User-" + (1 + r.nextInt(3)));
				values.put("queue", "queue" + (1 + r.nextInt(3)));
				values.put("cores", "" + (1 + r.nextInt(1024)));
				values.put("mem", (1 + r.nextInt(3) * 500) + "mb");
				values.put("walltimereq",
						DateUtil.secondsToString(r.nextInt(3600 * 24 * 5)));
				JobState state = JobState.values()[r
						.nextInt(JobState.values().length - 1)];
				if (state == JobState.RUNNING) {
					values.put("walltimeused",
							DateUtil.secondsToString(r.nextInt(3600 * 24 * 2)));
				}
				values.put("state", state.getCode());
				Date date1 = new Date(lowerDate
						+ r.nextInt(1000 * 3600 * 24 * 10));
				values.put("date1", DateUtil.QStatFormatDateTime(date1));
				values.put(
						"date2",
						DateUtil.QStatFormatDateTime(new Date(date1.getTime()
								+ r.nextInt(1000 * 3600 * 24 * 5))));

				jobs.append(replaceValues(template, values));

			}
			return super.parseJobs(jobs.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<JobInfo>();
	}

	private String replaceValues(String template, HashMap<String, String> values) {
		for (String key : values.keySet()) {
			template = template.replaceAll("#" + key + "#", values.get(key));
		}
		return template;
	}
}
