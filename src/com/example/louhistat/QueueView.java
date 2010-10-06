package com.example.louhistat;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class QueueView extends VerticalLayout {

	private QueueTable queueTable;
	private Button refreshButton;

	public QueueView() {
		construct();
		queueTable.repopulate();
	}

	private void construct() {
		queueTable = new QueueTable();
		refreshButton = new Button("Refresh", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				refreshQueueTable();
			}
		});

		addComponent(queueTable);
		addComponent(refreshButton);

	}

	protected void refreshQueueTable() {
		queueTable.repopulate();
	}
}
