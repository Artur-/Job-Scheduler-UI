package org.vaadin.artur.jobschedulerui;

import java.util.List;

import org.vaadin.artur.jobschedulerui.server.ServerConnector;
import org.vaadin.artur.jobschedulerui.server.data.QueueInfo;
import org.vaadin.artur.jobschedulerui.ui.data.ByteSuffixString;
import org.vaadin.artur.jobschedulerui.ui.data.TimeString;
import org.vaadin.artur.jobschedulerui.ui.util.TableUtil;

import com.jcraft.jsch.JSchException;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;

public class QueuesTable extends Table implements CellStyleGenerator {

	private static final Object QUEUE_NAME = "Queue name";
	private static final Object JOBS = "Total jobs";
	private static final Object JOBS_RUNNING = "Jobs running";
	private static final Object RESTRICTED = "Restricted";
	private static final Object MAX_MEMORY = "Max memory";
	private static final Object MAX_CORES = "Max cores";
	private static final Object MAX_RUN_TIME = "Max run time";
	private static final Object ENABLED = "Enabled";

	public QueuesTable() {
		setCaption("Available queues");
		init();
	}

	private void init() {
		// Do not use lazy loading
		setCacheRate(100000);
		setPageLength(0);

		setCellStyleGenerator(this);
	}

	private static Container createContainer() {
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(QUEUE_NAME, String.class, "");
		ic.addContainerProperty(JOBS, Integer.class, "");
		ic.addContainerProperty(JOBS_RUNNING, Integer.class, "");
		// Can't use boolean because "conversion" for a boolean to a boolean
		// will fail
		ic.addContainerProperty(RESTRICTED, Boolean.class, false);
		ic.addContainerProperty(MAX_MEMORY, ByteSuffixString.class, "");
		ic.addContainerProperty(MAX_CORES, Integer.class, "");
		ic.addContainerProperty(MAX_RUN_TIME, TimeString.class, "");
		ic.addContainerProperty(ENABLED, Boolean.class, "");

		return ic;
	}

	private void setTableDefaults() {
		setColumnAlignment(MAX_CORES, ALIGN_RIGHT);
		setColumnAlignment(MAX_MEMORY, ALIGN_RIGHT);
		setColumnAlignment(MAX_RUN_TIME, ALIGN_RIGHT);
	}

	private static void populate(Container c, List<QueueInfo> queueInfos) {
		for (QueueInfo qi : queueInfos) {
			Item i = c.addItem(qi);
			i.getItemProperty(QUEUE_NAME).setValue(qi.getQueueName());
			i.getItemProperty(JOBS).setValue(qi.getJobsTotal());

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
		TableUtil.showWaitText(this);

		new Thread(new QueuePopulator(JobSchedulerUIApplication.get(),
				ServerConnector.get())).start();
	}

	private class QueuePopulator implements Runnable {

		private JobSchedulerUIApplication app;
		private ServerConnector serverConnector;

		public QueuePopulator(JobSchedulerUIApplication app,
				ServerConnector serverConnector) {
			this.app = app;
			this.serverConnector = serverConnector;
		}

		@Override
		public void run() {
			try {
				List<QueueInfo> queueInfos = serverConnector
						.getAvailableQueues();
				synchronized (app) {
					Container c = createContainer();
					populate(c, queueInfos);
					setContainerDataSource(c);
					setTableDefaults();
				}
			} catch (JSchException e) {
				TableUtil.showErrorMessage(QueuesTable.this,
						"Error fetching data", e);
			}
			app.push();

		}
	}

	public String getStyle(Object itemId, Object propertyId) {
		if (!(itemId instanceof QueueInfo)) {
			return null;
		}

		QueueInfo qi = ((QueueInfo) itemId);

		if (propertyId == null) {
			// Row style
			String style;
			if (qi.hasAccess(JobSchedulerUIApplication.get()
					.getServerConnector().getUserName())) {
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
