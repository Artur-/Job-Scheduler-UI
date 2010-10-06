package org.vaadin.artur.jobschedulerui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.vaadin.artur.jobschedulerui.server.ServerConfiguration;
import org.vaadin.artur.jobschedulerui.server.ServerConnector;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Window;

public class JobSchedulerUIApplication extends Application implements
		HttpServletRequestListener {

	private ServerConfiguration serverConfig;
	private ServerConnector serverConnector;
	private static ThreadLocal<JobSchedulerUIApplication> app = new ThreadLocal<JobSchedulerUIApplication>();

	@Override
	public void init() {
		serverConfig = new ServerConfiguration();

		Window mainWindow = new Window("Queue System Status", new LoginView());
		setMainWindow(mainWindow);
		setTheme("jobschedulerui");
	}

	public void login(ServerConnector sc) {
		serverConnector = sc;
		getMainWindow().setContent(new MainView());

	}

	@Override
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		app.set(this);
	}

	@Override
	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		app.set(null);
	}

	public static JobSchedulerUIApplication get() {
		return app.get();
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfig;
	}

	public ServerConnector getServerConnector() {
		return serverConnector;
	}

	public void push() {
		ComponentContainer mainContent = getMainWindow().getContent();
		if (mainContent instanceof MainView) {
			((MainView) mainContent).push();
		}
	}

	@Override
	public void close() {
		if (serverConnector != null) {
			serverConnector.cleanup();
		}
		super.close();

	}
}
