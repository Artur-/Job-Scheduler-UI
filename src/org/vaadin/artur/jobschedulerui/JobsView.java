package org.vaadin.artur.jobschedulerui;

import org.vaadin.artur.jobschedulerui.JobsTable.Filter;
import org.vaadin.artur.jobschedulerui.JobsTable.Highlight;
import org.vaadin.artur.jobschedulerui.JobsTable.RepopulatedListener;
import org.vaadin.artur.jobschedulerui.RefreshCheckerThread.RefreshEvent;
import org.vaadin.artur.jobschedulerui.RefreshCheckerThread.RefreshListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class JobsView extends VerticalLayout implements RefreshListener {

	private JobsTable jobsTable;
	private Button refreshButton;
	private NativeSelect highlightSelect;
	private NativeSelect filterSelect;
	private TextField username;
	private TextField refreshIntervalTextfield;

	private transient RefreshCheckerThread refreshCheckerThread;

	public JobsView() {
		setHeight("100%");
		construct();
		jobsTable.repopulate(null);
	}

	private void construct() {
		jobsTable = new JobsTable(this);
		jobsTable.setSizeFull();

		HorizontalLayout controls = new HorizontalLayout();
		HorizontalLayout refreshControls = new HorizontalLayout();
		refreshControls.setWidth("100%");

		highlightSelect = createHighlightSelect();
		filterSelect = createFilterSelect();

		username = new TextField("Username (debug only)",
				JobSchedulerUIApplication.get().getServerConnector()
						.getUserName());
		username.setImmediate(true);
		username.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				jobsTable.setFilter((Filter) filterSelect.getValue());
				jobsTable.setHighlight((Highlight) highlightSelect.getValue());
			}
		});
		refreshButton = new Button("Refresh", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				refreshCheckerThread.reset();
				refreshJobsTable();
			}
		});

		createAndStartRefreshChecker();
		refreshIntervalTextfield = new TextField("Refresh interval (s)");
		refreshIntervalTextfield.setColumns(3);
		refreshIntervalTextfield.setValue(String.valueOf(refreshCheckerThread
				.getRefreshInterval()));
		refreshIntervalTextfield.setImmediate(true);
		refreshIntervalTextfield.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				refreshCheckerThread.setRefreshInterval(Integer
						.parseInt((String) event.getProperty().getValue()));
			}
		});

		addComponent(controls);
		addComponent(jobsTable);
		addComponent(refreshControls);
		setExpandRatio(jobsTable, 1);
		refreshControls.addComponent(refreshButton);
		refreshControls.addComponent(refreshIntervalTextfield);
		// controls.addComponent(username);
		refreshControls.setComponentAlignment(refreshButton,
				Alignment.BOTTOM_LEFT);
		// Hack to enable showing the full caption
		refreshControls.setExpandRatio(refreshIntervalTextfield, 1);
		controls.addComponent(highlightSelect);
		controls.addComponent(filterSelect);

	}

	private NativeSelect createHighlightSelect() {
		NativeSelect highlightSelect = new NativeSelect("Highlight");
		highlightSelect.setNullSelectionAllowed(false);

		for (Highlight h : Highlight.values()) {
			highlightSelect.addItem(h);
		}
		highlightSelect.setValue(Highlight.ALL);
		highlightSelect.setImmediate(true);
		highlightSelect.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Highlight highlight = ((Highlight) event.getProperty()
						.getValue());
				jobsTable.setHighlight(highlight);
			}
		});

		return highlightSelect;
	}

	private NativeSelect createFilterSelect() {
		NativeSelect filterSelect = new NativeSelect("Show");
		filterSelect.setNullSelectionAllowed(false);
		for (Filter f : Filter.values()) {
			filterSelect.addItem(f);
		}
		filterSelect.setValue(Filter.ALL);
		filterSelect.setImmediate(true);
		filterSelect.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Filter f = ((Filter) event.getProperty().getValue());
				jobsTable.setFilter(f);
			}
		});

		return filterSelect;
	}

	protected void refreshJobsTable() {
		refreshButton.setEnabled(false);
		// jobsTable.setEnabled(false);
		jobsTable.repopulate(new RepopulatedListener() {

			@Override
			public void repopulated() {
				refreshButton.setEnabled(true);
				// jobsTable.setEnabled(true);
			}
		});
		((JobSchedulerUIApplication) getApplication()).push();
	}

	public String getUsername() {
		return (String) username.getValue();
	}

	@Override
	public void doRefresh(RefreshEvent event) {
		refreshJobsTable();
	}

	@Override
	public void attach() {
		super.attach();
		System.out.println("Attach");
		createAndStartRefreshChecker();
	}

	private void createAndStartRefreshChecker() {
		if (refreshCheckerThread != null && refreshCheckerThread.isAlive()) {
			return;
		}

		System.out.println("Create refresh checker");
		refreshCheckerThread = new RefreshCheckerThread();
		refreshCheckerThread.addListener(this);
		refreshCheckerThread.start();
	}

	@Override
	public void detach() {
		System.out.println("Detach");
		super.detach();
		if (refreshCheckerThread != null) {
			System.out.println("Terminating refreshChecker");
			refreshCheckerThread.terminate();
			refreshCheckerThread = null;
		}

	}
}
