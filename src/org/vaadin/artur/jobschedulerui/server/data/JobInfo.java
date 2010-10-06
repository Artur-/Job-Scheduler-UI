package org.vaadin.artur.jobschedulerui.server.data;

import java.util.Date;

public abstract class JobInfo extends GenericInfo {

	private String identifier;
	private String name;
	private String comment;
	private String owner;
	private String queue;

	private Date estimatedStartTime;
	private Date startTime;
	private Date submitTime;

	private Integer secondsReserved;
	private Integer secondsUsed;
	private Integer coresRequested;
	private Integer memoryPerCoreRequested;

	private JobState state = JobState.UNKNOWN;

	public JobInfo() {
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/* Generic methods */

	public Integer getSecondsRemaining() {
		if (!getState().isRunning()) {
			return -1;
		}

		Integer reserved = getSecondsReserved();
		Integer used = getSecondsUsed();
		if (reserved == null || used == null) {
			return -1;
		}

		return reserved - used;
	}

	public Integer getCpuHoursUsed() {
		if (!getState().isRunning()) {
			return 0;
		}
		Integer used = getSecondsUsed();
		Integer cores = getCoresRequested();
		if (used == null || cores == null) {
			return 0;
		}

		return (int) (used / 3600.0 * cores);
	}

	/* Getters & setters */
	public Integer getMemoryPerCoreRequested() {
		return memoryPerCoreRequested;
	}

	public void setMemoryPerCoreRequested(Integer memoryPerCoreRequested) {
		this.memoryPerCoreRequested = memoryPerCoreRequested;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEstimatedStartTime() {
		return estimatedStartTime;
	}

	public void setEstimatedStartTime(Date estimatedStartTime) {
		this.estimatedStartTime = estimatedStartTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getSecondsReserved() {
		return secondsReserved;
	}

	public void setSecondsReserved(Integer secondsReserved) {
		this.secondsReserved = secondsReserved;
	}

	public Integer getSecondsUsed() {
		return secondsUsed;
	}

	public void setSecondsUsed(Integer secondsUsed) {
		this.secondsUsed = secondsUsed;
	}

	public Integer getCoresRequested() {
		return coresRequested;
	}

	public void setCoresRequested(Integer coresRequested) {
		this.coresRequested = coresRequested;
	}

	public JobState getState() {
		return state;
	}

	public void setState(JobState state) {
		this.state = state;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

}
