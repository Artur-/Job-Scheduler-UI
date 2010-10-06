package org.vaadin.artur.jobschedulerui;

import java.util.Date;

import org.vaadin.artur.jobschedulerui.ui.data.DateTimeString;


public class EstimatedDateTimeString extends DateTimeString {

	public EstimatedDateTimeString(Date time) {
		super(time);
	}

	@Override
	public String toString() {
		if (getTime() == 0) {
			return "?";
		}

		return "Est: " + super.toString();
	}

}
