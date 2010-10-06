package com.example.louhistat;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class JobsView extends VerticalLayout {

	private JobsTable jobsTable;
	private Button refreshButton;

	public JobsView() {
		construct();
		jobsTable.repopulate();
	}

	private void construct() {
		jobsTable = new JobsTable();
		refreshButton = new Button("Refresh", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				refreshJobsTable();
			}
		});

		addComponent(jobsTable);
		addComponent(refreshButton);

	}

	protected void refreshQueueTable() {
		jobsTable.repopulate();
	}
}
