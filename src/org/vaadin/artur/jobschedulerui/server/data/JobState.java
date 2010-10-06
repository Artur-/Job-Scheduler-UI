package org.vaadin.artur.jobschedulerui.server.data;

public enum JobState {
	RUNNING("R", "Running"), QUEUED("Q", "Queued"), ON_HOLD("H", "On Hold"), UNKNOWN(
			"?", "?");

	private String code;
	private String description;

	private JobState(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public boolean isRunning() {
		return this == RUNNING;
	}

	public boolean isOnHold() {
		return this == ON_HOLD;
	}

	public boolean isQueued() {
		return this == QUEUED;
	}

	public static JobState getState(String code) {
		if (code == null) {
			return UNKNOWN;
		}

		for (JobState s : values()) {
			if (s.code.equals(code)) {
				return s;
			}
		}
		return UNKNOWN;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Gets the next state after the state parameter, excluding UNKNOWN.
	 * 
	 * @param state
	 * @return
	 */
	public static JobState next(JobState state) {
		switch (state) {
		case RUNNING:
			return QUEUED;
		case QUEUED:
			return ON_HOLD;
		default:
			return RUNNING;
		}
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public String getCode() {
		return code;
	}
}
