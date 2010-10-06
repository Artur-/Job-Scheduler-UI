package org.vaadin.artur.jobschedulerui;

import org.vaadin.artur.icepush.ICEPush;
import org.vaadin.artur.jobschedulerui.ui.MainMenu;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MainView extends VerticalLayout {

	private VerticalLayout contentWrapper = new VerticalLayout();
	private Label viewHeader = new Label("", Label.CONTENT_XHTML);
	private MainMenu mainMenu = new MainMenu(this);
	private ICEPush icePush = new ICEPush();
	private QueuesView queuesView;
	private JobsView jobsView;

	public MainView() {
		super();
		setSizeFull();

		contentWrapper.setSizeFull();

		contentWrapper.setMargin(true);
		addComponent(icePush);
		addComponent(mainMenu);
		contentWrapper.addComponent(viewHeader);
		addComponent(contentWrapper);
		setExpandRatio(contentWrapper, 1);

		showJobs();
		// jobsView.refreshJobsTable();
	}

	public void push() {
		icePush.push();
	}

	public void showJobs() {
		try {
			contentWrapper.removeComponent(contentWrapper.getComponent(1));
		} catch (Exception e) {
			// Assume the component was not there.
			// FIXME: use contentWrapper.size() once it exists

		}
		contentWrapper.addComponent(getJobsView());
		contentWrapper.setExpandRatio(getJobsView(), 1);

		String header = "Queued and running jobs at "
				+ JobSchedulerUIApplication.get().getServerConnector()
						.getHostName() + "</h2>";
		viewHeader.setValue("<h2>" + header + "</h2>");

	}

	public void showQueues() {
		try {
			contentWrapper.removeComponent(contentWrapper.getComponent(1));
		} catch (Exception e) {
			// Assume the component was not there.
			// FIXME: use contentWrapper.size() once it exists

		}
		contentWrapper.addComponent(getQueuesView());
		contentWrapper.setExpandRatio(getQueuesView(), 1);

		String header = "Queues available at "
				+ JobSchedulerUIApplication.get().getServerConnector()
						.getHostName() + "</h2>";
		viewHeader.setValue("<h2>" + header + "</h2>");

	}

	private Component getJobsView() {
		if (jobsView == null) {
			jobsView = new JobsView();
		}
		return jobsView;
	}

	private Component getQueuesView() {
		if (queuesView == null) {
			queuesView = new QueuesView();
		}
		return queuesView;
	}

}
