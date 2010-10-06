package org.vaadin.artur.jobschedulerui.server.data;

import java.util.Set;

public abstract class QueueInfo extends GenericInfo {

	private String queueName;
	private Integer priority;
	private Integer maximumRunTimeInSeconds;
	private Set<String> usersWithAccess;
	private boolean enabled;

	private Integer jobsRunning;
	private Integer jobsTotal;

	private Integer maximumCores;
	private Integer minimumCores;
	private Integer maximumMemory;

	private String comment;

	public QueueInfo() {
		super();
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getQueueName() {
		return queueName;
	}

	public Integer getMaximumRunTimeInSeconds() {
		return maximumRunTimeInSeconds;
	}

	public void setMaximumRunTimeInSeconds(Integer maximumRunTimeInSeconds) {
		this.maximumRunTimeInSeconds = maximumRunTimeInSeconds;
	}

	public Integer getJobsRunning() {
		return jobsRunning;
	}

	public void setJobsRunning(Integer jobsRunning) {
		this.jobsRunning = jobsRunning;
	}

	public Set<String> getUsersWithAccess() {
		return usersWithAccess;
	}

	public void setUsersWithAccess(Set<String> usersWithAccess) {
		this.usersWithAccess = usersWithAccess;
	}

	public boolean isRestricted() {
		return usersWithAccess != null;
	}

	public boolean hasAccess(String username) {
		if (!isRestricted()) {
			return true;
		}
		return getUsersWithAccess().contains(username);

	}

	public Integer getMaximumCores() {
		return maximumCores;
	}

	public void setMaximumCores(Integer maximumCores) {
		this.maximumCores = maximumCores;
	}

	public Integer getMinimumCores() {
		return minimumCores;
	}

	public void setMinimumCores(Integer minimumCores) {
		this.minimumCores = minimumCores;
	}

	public Integer getMaximumMemory() {
		return maximumMemory;
	}

	public void setMaximumMemory(Integer maximumMemory) {
		this.maximumMemory = maximumMemory;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getJobsTotal() {
		return jobsTotal;
	}

	public void setJobsTotal(Integer jobsTotal) {
		this.jobsTotal = jobsTotal;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}
