package com.example.louhistat;

import java.util.ArrayList;
import java.util.List;

import com.example.louhistat.server.ServerConnector;
import com.example.louhistat.server.data.QueueInfo;
import com.example.louhistat.ui.data.ByteSuffixString;
import com.example.louhistat.ui.data.TimeString;
import com.jcraft.jsch.JSchException;
import com.vaadin.data.Item;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;

public class QueueTable extends Table implements CellStyleGenerator {

	private static final Object QUEUE_NAME = "Queue name";
	private static final Object JOBS = "Total jobs";
	private static final Object JOBS_RUNNING = "Jobs running";
	private static final Object RESTRICTED = "Restricted";
	private static final Object MAX_MEMORY = "Max memory";
	private static final Object MAX_CORES = "Max cores";
	private static final Object MAX_RUN_TIME = "Max run time";
	private static final Object ENABLED = "Enabled";

	public QueueTable() {
		setCaption("Available queues");
		init();
	}

	private void init() {
		// Do not use lazy loading
		setCacheRate(100000);
		setPageLength(0);

		setCellStyleGenerator(this);

		addContainerProperty(QUEUE_NAME, String.class, "");
		addContainerProperty(JOBS, Integer.class, "");
		addContainerProperty(JOBS_RUNNING, Integer.class, "");
		// Can't use boolean because "conversion" for a boolean to a boolean
		// will fail
		addContainerProperty(RESTRICTED, Boolean.class, false);
		addContainerProperty(MAX_MEMORY, ByteSuffixString.class, "");
		addContainerProperty(MAX_CORES, Integer.class, "");
		addContainerProperty(MAX_RUN_TIME, TimeString.class, "");
		addContainerProperty(ENABLED, Boolean.class, "");

		setColumnAlignment(MAX_CORES, ALIGN_RIGHT);
		setColumnAlignment(MAX_MEMORY, ALIGN_RIGHT);
		setColumnAlignment(MAX_RUN_TIME, ALIGN_RIGHT);

	}

	private void populate(List<QueueInfo> queueInfos) {
		removeAllItems();
		for (QueueInfo qi : queueInfos) {
			Item i = addItem(qi);
			i.getItemProperty(QUEUE_NAME).setValue(qi.getQueueName());
			i.getItemProperty(JOBS).setValue(qi.getParameter("total_jobs"));

			i.getItemProperty(JOBS_RUNNING).setValue(qi.getJobsRunning());
			i.getItemProperty(RESTRICTED).setValue(qi.isRestricted());
			i.getItemProperty(MAX_MEMORY).setValue(
					new ByteSuffixString(qi.getMaximumMemory()));
			i.getItemProperty(MAX_CORES).setValue(qi.getMaximumCores());
			i.getItemProperty(MAX_RUN_TIME).setValue(
					new TimeString(qi.getMaximumRunTimeInSeconds()));
			i.getItemProperty(ENABLED).setValue(qi.isEnabled());
		}
	}

	public void repopulate() {
		try {
			List<QueueInfo> queueInfos = ServerConnector.get()
					.getAvailableQueues();
			populate(queueInfos);
		} catch (JSchException e) {
			List<QueueInfo> error = new ArrayList<QueueInfo>();
			QueueInfo qi = new QueueInfo("Error fetching data: "
					+ e.getMessage());
			error.add(qi);
			populate(error);
		}

	}

	public String getStyle(Object itemId, Object propertyId) {
		if (propertyId == null) {
			// Row style
			QueueInfo qi = ((QueueInfo) itemId);
			String style;
			if (qi.hasAccess(LouhistatApplication.get().getServerConnector()
					.getUserName())) {
				style = "has-access";
			} else {
				style = "restricted";
			}
			if (!qi.isEnabled()) {
				style += " disabled";
			}

			return style;
		}
		return null;
	}
}
