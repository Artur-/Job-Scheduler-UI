package com.example.louhistat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.louhistat.server.ServerConfiguration;
import com.example.louhistat.server.ServerConnector;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

public class LouhistatApplication extends Application implements
		HttpServletRequestListener {

	private ServerConfiguration serverConfig;
	private ServerConnector serverConnector;
	private static ThreadLocal<LouhistatApplication> app = new ThreadLocal<LouhistatApplication>();

	@Override
	public void init() {
		serverConfig = new ServerConfiguration();
		// serverConfig.setHostName("tiger.dnsdojo.com");
		serverConfig.setHostName("louhi.csc.fi");

		Window mainWindow = new Window("Louhistat Application", new LoginView());
		setMainWindow(mainWindow);
		setTheme("louhistattheme");
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

	public static LouhistatApplication get() {
		return app.get();
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfig;
	}

	public ServerConnector getServerConnector() {
		return serverConnector;
	}
}
