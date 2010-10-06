package com.example.louhistat;

import com.example.louhistat.server.Credentials;
import com.example.louhistat.server.ServerConfiguration;
import com.example.louhistat.server.ServerConnector;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends VerticalLayout {

	private TextField username;
	private TextField password;
	private String hostname;
	private VerticalLayout layout;

	public LoginView() {
		super();
		hostname = ServerConfiguration.get().getHostName();

		setSizeFull();
		populateView();
	}

	private void populateView() {
		layout = new VerticalLayout();
		layout.setMargin(true);

		Panel panel = new Panel(layout);
		panel.setCaption("Credentials for " + hostname);

		panel.setSizeUndefined();
		layout.setSizeUndefined();

		username = new TextField("Username", "artur");
		username.setRequired(true);
		username.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER,
				new int[] {}) {

			@Override
			public void handleAction(Object sender, Object target) {
				password.focus();

			}
		});

		password = new TextField("Password");
		password.setSecret(true);
		password.focus();
		password.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER,
				new int[] {}) {

			@Override
			public void handleAction(Object sender, Object target) {
				login();

			}
		});
		password.setRequired(true);
		Button loginButton = new Button("Login", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				login();
			}
		});

		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
		layout.addComponent(username);
		layout.addComponent(password);
		layout.addComponent(loginButton);

	}

	public void login() {
		Credentials creds = new Credentials((String) username.getValue(),
				(String) password.getValue());

		ServerConnector sc = new ServerConnector();
		sc.setHostname(hostname);
		sc.setCredentials(creds);

		boolean ok = sc.verifyCredentials();

		if (!ok) {
			getWindow().showNotification(
					"Login failed. Check the username and password");
			return;
		}

		// Credentials ok, moving on...
		LouhistatApplication.get().login(sc);
	}
}
