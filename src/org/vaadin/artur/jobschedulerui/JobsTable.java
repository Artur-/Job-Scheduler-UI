package org.vaadin.artur.jobschedulerui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vaadin.artur.jobschedulerui.server.ServerConnector;
import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.JobState;
import org.vaadin.artur.jobschedulerui.ui.data.DateTimeString;
import org.vaadin.artur.jobschedulerui.ui.data.FormattedInteger;
import org.vaadin.artur.jobschedulerui.ui.data.TimeString;
import org.vaadin.artur.jobschedulerui.ui.util.TableUtil;

import com.jcraft.jsch.JSchException;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.FooterClickListener;

public class JobsTable extends Table implements CellStyleGenerator,
		FooterClickListener {

	private static final String JOB_ID = "Job id";
	private static final String OWNER = "Owner";
	private static final String NAME = "Name";
	private static final String STATUS = "Status";
	private static final String CORES_REQUESTED = "Cores Requested";
	private static final String CPU_H_USED = "CPU hours used";
	private static final String START_TIME = "Start time";
	private static final String MAX_TIME_REMAINING = "Max time remaining";
	private static final String COMMENT = "Comment";

	public enum Filter {
		ALL("All"), OWN("Own"), RUNNING("Running"), NOTRUNNING("Pending");
		private String description;

		private Filter(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	public enum Highlight {
		ALL("All"), OWN("Own"), NONE("None");
		private String description;

		private Highlight(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	private Highlight highlight = Highlight.ALL;
	private Filter filter = Filter.ALL;
	private JobState sumState = JobState.RUNNING;
	private JobsView jobsView;
	private boolean repopulateInProgress = false;

	public JobsTable(JobsView jobsView) {
		this.jobsView = jobsView;
		setCaption("Submitted jobs");
		init();
	}

	private void init() {
		setPageLength(40);
		setStyleName("jobstable");
		setCellStyleGenerator(this);
		addListener((FooterClickListener) this);
	}

	private Container createContainer() {
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(JOB_ID, String.class, "");
		ic.addContainerProperty(OWNER, String.class, "");
		ic.addContainerProperty(NAME, String.class, "");
		ic.addContainerProperty(STATUS, JobState.class, "");
		ic.addContainerProperty(CORES_REQUESTED, FormattedInteger.class, "");
		ic.addContainerProperty(CPU_H_USED, FormattedInteger.class, "");
		ic.addContainerProperty(START_TIME, String.class, "");
		ic.addContainerProperty(MAX_TIME_REMAINING, TimeString.class, "");

		ic.addContainerProperty(COMMENT, String.class, "");

		return ic;
	}

	private void setAlignments() {
		setColumnAlignment(CPU_H_USED, ALIGN_RIGHT);
		setColumnAlignment(CORES_REQUESTED, ALIGN_RIGHT);
		setColumnAlignment(MAX_TIME_REMAINING, ALIGN_RIGHT);
	}

	private void updateFooter() {
		setFooterVisible(true);
		setColumnFooter(JOB_ID,
				"Jobs: " + new FormattedInteger(size()).toString());
		setColumnFooter(OWNER,
				"Owners: " + new FormattedInteger(unique(OWNER)).toString());

		setColumnFooter(CORES_REQUESTED, "Cores in use: "
				+ new FormattedInteger(sum(CORES_REQUESTED)));
		setColumnFooter(STATUS, sumState.getDescription() + ": "
				+ new FormattedInteger(sum(sumState)).toString());
		setColumnFooter(MAX_TIME_REMAINING, nextFree());

	}

	private int unique(String propertyId) {
		Set<Object> found = new HashSet<Object>();

		for (Object id : getItemIds()) {
			Object value = getContainerProperty(id, propertyId).getValue();
			found.add(value);
		}

		return found.size();

	}

	private String nextFree() {
		TimeString minTime = minTime(MAX_TIME_REMAINING);
		return "Next slot in " + minTime;
	}

	private TimeString minTime(String timeStringColumn) {
		TimeString minTime = null;
		for (Object id : getItemIds()) {
			TimeString time = (TimeString) getContainerProperty(id,
					timeStringColumn).getValue();

			if (time.getTime() < 0) {
				continue;
			}
			if (minTime == null || (time.getTime() < minTime.getTime())) {
				minTime = time;
			}
		}

		return minTime;
	}

	private int sum(JobState sumState) {
		if (!getContainerPropertyIds().contains(STATUS)) {
			return 0;
		}

		int sum = 0;
		for (Object id : getItemIds()) {
			JobState state = (JobState) getContainerProperty(id, STATUS)
					.getValue();
			if (state == sumState) {
				sum++;
			}
		}

		return sum;
	}

	private int sum(String integerPropertyId) {
		if (!getContainerPropertyIds().contains(integerPropertyId)) {
			return 0;
		}
		int sum = 0;
		for (Object id : getItemIds()) {
			FormattedInteger i = (FormattedInteger) getContainerProperty(id,
					integerPropertyId).getValue();
			if (i != null && i.getValue() != null) {
				sum += i.getValue();
			}
		}
		return sum;
	}

	private static void populate(Container c, List<JobInfo> jobInfos) {
		for (JobInfo ji : jobInfos) {
			Item i = c.addItem(ji);
			i.getItemProperty(JOB_ID).setValue(ji.getIdentifier());
			i.getItemProperty(OWNER).setValue(ji.getOwner());
			i.getItemProperty(NAME).setValue(ji.getName());
			i.getItemProperty(STATUS).setValue(ji.getState());
			if (ji.getState().isRunning()) {
				i.getItemProperty(START_TIME).setValue(
						new DateTimeString(ji.getStartTime()));
			} else {
				i.getItemProperty(START_TIME)
						.setValue(
								new EstimatedDateTimeString(ji
										.getEstimatedStartTime()));

			}
			i.getItemProperty(MAX_TIME_REMAINING).setValue(
					new TimeString(ji.getSecondsRemaining()));
			i.getItemProperty(COMMENT).setValue(ji.getComment());
			i.getItemProperty(CORES_REQUESTED).setValue(
					new FormattedInteger(ji.getCoresRequested()));
			i.getItemProperty(CPU_H_USED).setValue(
					new FormattedInteger(ji.getCpuHoursUsed()));
		}
	}

	public interface RepopulatedListener {
		public void repopulated();
	}

	public void repopulate(RepopulatedListener rpl) {
		if (repopulateInProgress) {
			return;
		}

		repopulateInProgress = true;

		if (size() < 2) {
			// Only show when there is no previous data
			TableUtil.showWaitText(this);
		}
		JobSchedulerUIApplication app = (JobSchedulerUIApplication) getApplication();
		if (app == null) {
			app = JobSchedulerUIApplication.get();
		}

		new Thread(new JobPopulator(app, rpl)).start();

	}

	private class JobPopulator implements Runnable {

		private JobSchedulerUIApplication app;
		private ServerConnector serverConnector;
		private RepopulatedListener rpl;

		public JobPopulator(JobSchedulerUIApplication app,
				RepopulatedListener rpl) {
			this.app = app;
			serverConnector = app.getServerConnector();
			this.rpl = rpl;
		}

		@Override
		public void run() {
			try {
				reloadAndPopulate();
				// Wait for icepush to get up and running
				Thread.sleep(1000);

				app.push();
			} catch (JSchException e) {
				TableUtil.showErrorMessage(JobsTable.this,
						"Error fetching data", e);
			} catch (InterruptedException e) {
			} finally {
				if (rpl != null) {
					rpl.repopulated();
				}
				repopulateInProgress = false;
			}

		}

		private void reloadAndPopulate() throws JSchException {
			List<JobInfo> jobInfos = serverConnector.getJobs();
			Container c = createContainer();
			populate(c, jobInfos);
			synchronized (app) {
				setContainerDataSource(c);
				setAlignments();
				updateFooter();
				// Container is changed so filtering needs to be reapplied
				setFilter(filter);
				setColumnExpandRatio(COMMENT, 1);
				// Cache all rows when there are only a few of them
				if (c.size() < 200) {
					setCacheRate(1000);
				} else {
					setCacheRate(2);
				}
			}
		}
	}

	public String getStyle(Object itemId, Object propertyId) {
		if (!(itemId instanceof JobInfo)) {
			return null;
		}

		JobInfo ji = ((JobInfo) itemId);
		if (propertyId == null) {
			// Row style
			String style = "";

			boolean isOwner = ji.getOwner().equals(getUsername());

			if (isOwner) {
				style += " owner";
			}

			if (highlight == Highlight.ALL
					|| (isOwner && highlight == Highlight.OWN)) {
				if (ji.getState().isRunning()) {
					style += " running";
				} else if (ji.getState().isOnHold()) {
					style += " onhold";
				} else if (ji.getState().isQueued()) {
					style += " queued";
				}
			}

			return style;
		}
		return null;
	}

	private String getUsername() {
		return jobsView.getUsername();
	}

	public void setHighlight(Highlight highlight) {
		this.highlight = highlight;
		requestRepaint();
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
		IndexedContainer ic = ((IndexedContainer) getContainerDataSource());
		ic.removeContainerFilters(OWNER);
		ic.removeContainerFilters(STATUS);
		if (filter == Filter.OWN) {
			ic.addContainerFilter(OWNER, getUsername(), true, true);
		} else if (filter == Filter.RUNNING) {
			// FIXME: Depends on value
			ic.addContainerFilter(STATUS, JobState.RUNNING.getDescription(),
					true, true);
		} else if (filter == Filter.NOTRUNNING) {
			// FIXME: Can't do
			// ic.addContainerFilter(STATUS, "Running", true, true);
		}

		requestRepaint();

	}

	@Override
	public void footerClick(FooterClickEvent event) {
		if (event.getPropertyId().equals(STATUS)) {
			sumState = JobState.next(sumState);
			updateFooter();
		}
	}
}
