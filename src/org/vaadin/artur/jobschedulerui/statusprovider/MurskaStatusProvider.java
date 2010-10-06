package org.vaadin.artur.jobschedulerui.statusprovider;

public class MurskaStatusProvider extends SlurmLSFStatusProvider {

	private String lsfProfile() {
		return ". /opt/hptc/lsf/top/env/profile.lsf";
	}

	@Override
	public String getAvailableQueuesCommand() {
		return lsfProfile() + " ; " + super.getAvailableQueuesCommand();
	}

	@Override
	public String getListJobsCommand() {
		return lsfProfile() + " ; " + super.getListJobsCommand();
	}

}
