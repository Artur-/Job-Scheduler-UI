package com.example.louhistat;

import com.vaadin.ui.TabSheet;

public class MainTabSheet extends TabSheet {

	private QueueView queueView;
	private JobsView jobsView;

	public MainTabSheet() {
		construct();
	}

	private void construct() {
		queueView = new QueueView();
		jobsView = new JobsView();

		addTab(queueView, "Available Queues", null);
		addTab(jobsView, "Jobs", null);
	}

}
