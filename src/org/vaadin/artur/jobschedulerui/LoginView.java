package org.vaadin.artur.jobschedulerui;

import org.vaadin.artur.jobschedulerui.server.Credentials;
import org.vaadin.artur.jobschedulerui.server.ServerConnector;
import org.vaadin.artur.jobschedulerui.statusprovider.DummyStatusProvider;
import org.vaadin.artur.jobschedulerui.statusprovider.MurskaStatusProvider;
import org.vaadin.artur.jobschedulerui.statusprovider.PBSStatusProvider;
import org.vaadin.artur.jobschedulerui.statusprovider.StatusProvider;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

public class LoginView extends VerticalLayout {

	private static final Object CAPTION = "Caption";
	private TextField hostname;
	private TextField username;
	private TextField password;

	private VerticalLayout layout;
	private NativeSelect statusProviderSelect;

	public LoginView() {
		super();
		// hostname = ServerConfiguration.get().getHostName();
		setSizeFull();
		populateView();

	}

	private NativeSelect createStatusProviderSelect() {
		NativeSelect select = new NativeSelect("Host Queue System");
		select.setNullSelectionAllowed(false);

		select.addContainerProperty(CAPTION, String.class, "");
		select.setItemCaptionPropertyId(CAPTION);
		Item item;

		item = select.addItem(PBSStatusProvider.class);
		item.getItemProperty(CAPTION).setValue("PBS (Cray XT4/XT5)");
		item = select.addItem(MurskaStatusProvider.class);
		item.getItemProperty(CAPTION).setValue("Slurm/LSF");
		item = select.addItem(DummyStatusProvider.class);
		item.getItemProperty(CAPTION).setValue("Dummy");

		return select;
	}

	private void populateView() {
		layout = new VerticalLayout();
		layout.setMargin(true);

		Panel panel = new Panel(layout);
		panel.setCaption("Please log in");

		panel.setSizeUndefined();
		layout.setSizeUndefined();

		statusProviderSelect = createStatusProviderSelect();
		statusProviderSelect.setValue(DummyStatusProvider.class);
		statusProviderSelect.setRequired(true);
		statusProviderSelect.setImmediate(true);
		statusProviderSelect.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {

			}
		});

		hostname = new TextField("Hostname", "");
		hostname.setRequired(true);
		hostname.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER,
				new int[] {}) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (((String) hostname.getValue()).length() > 0) {
					username.focus();
				}
			}
		});
		username = new TextField("Username", "");
		username.setRequired(true);
		username.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER,
				new int[] {}) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (((String) username.getValue()).length() > 0) {
					password.focus();
				}
			}
		});

		password = new TextField("Password", "");
		password.setSecret(true);
		password.addShortcutListener(new ShortcutListener(null, KeyCode.ENTER,
				new int[] {}) {

			@Override
			public void handleAction(Object sender, Object target) {
				if (((String) password.getValue()).length() > 0) {
					login();
				}

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

		hostname.focus();

		layout.addComponent(statusProviderSelect);
		layout.addComponent(hostname);
		layout.addComponent(username);
		layout.addComponent(password);
		layout.addComponent(loginButton);

	}

	public void login() {
		Credentials creds = new Credentials((String) username.getValue(),
				(String) password.getValue());

		Class<StatusProvider> providerClass = (Class<StatusProvider>) statusProviderSelect
				.getValue();
		StatusProvider provider;
		try {
			provider = providerClass.newInstance();
		} catch (Exception e) {
			getWindow().showNotification(
					"Could not instansiate provider class "
							+ providerClass.getName(),
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}

		ServerConnector sc = new ServerConnector();
		sc.setHostname((String) hostname.getValue());
		sc.setCredentials(creds);
		sc.setStatusProvider(provider);

		boolean ok = sc.verifyCredentials();

		if (!ok) {
			getWindow().showNotification(
					"Login failed. Check the username and password",
					Notification.TYPE_ERROR_MESSAGE);
			return;
		}

		// Credentials ok, moving on...
		JobSchedulerUIApplication.get().login(sc);
	}
}
