package org.vaadin.artur.jobschedulerui.server;

import org.vaadin.artur.jobschedulerui.JobSchedulerUIApplication;

public class ServerConfiguration {

	private String hostName;

	public String getHostName() {
		return hostName;
	}

	public static ServerConfiguration get() {
		return JobSchedulerUIApplication.get().getServerConfiguration();
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

}
