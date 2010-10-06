package org.vaadin.artur.jobschedulerui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class QueuesView extends VerticalLayout {

	private QueuesTable queueTable;
	private Button refreshButton;

	public QueuesView() {
		setSizeFull();
		construct();
		queueTable.setSizeFull();
		queueTable.repopulate();
	}

	private void construct() {
		queueTable = new QueuesTable();
		refreshButton = new Button("Refresh", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				refreshQueueTable();
			}
		});

		addComponent(queueTable);
		addComponent(refreshButton);
		setExpandRatio(queueTable, 1);

	}

	protected void refreshQueueTable() {
		queueTable.repopulate();
	}
}
