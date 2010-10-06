package org.vaadin.artur.jobschedulerui.statusprovider;

import java.util.List;

import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.QueueInfo;


public interface StatusProvider {

	String getAvailableQueuesCommand();

	List<QueueInfo> parseQueues(String commandResult);

	String getListJobsCommand();

	List<JobInfo> parseJobs(String result);
}
