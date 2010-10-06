package org.vaadin.artur.jobschedulerui.ui;

import org.vaadin.artur.jobschedulerui.MainView;

import com.vaadin.ui.MenuBar;

public class MainMenu extends MenuBar {

	public MainMenu(final MainView mainView) {
		setWidth("100%");
		MenuItem viewMenu = addItem("Show", null);

		addItem("Log out", new Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) {
				getApplication().close();
			}

		});

		viewMenu.addItem("Jobs", new Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) {
				mainView.showJobs();
			}

		});
		viewMenu.addItem("Queues", new Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) {
				mainView.showQueues();
			}

		});

	}
}
