package org.vaadin.artur.jobschedulerui.server.data;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericInfo {
	private Map<String, String> parameters = new HashMap<String, String>();

	public GenericInfo() {

	}

	protected void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	protected String getParameter(String name) {
		return parameters.get(name);
	}

}
