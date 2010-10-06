package com.example.louhistat;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class MainView extends Panel {

	private VerticalLayout mainLayout = new VerticalLayout();
	private MainTabSheet mainTabSheet;

	public MainView() {
		super();
		setContent(mainLayout);
		setCaption("Status for "
				+ LouhistatApplication.get().getServerConfiguration()
						.getHostName());
		mainLayout.setMargin(true);
		mainTabSheet = new MainTabSheet();
		mainLayout.addComponent(mainTabSheet);
	}
}
