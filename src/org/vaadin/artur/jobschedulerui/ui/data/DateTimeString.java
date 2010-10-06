package org.vaadin.artur.jobschedulerui.ui.data;

import java.util.Date;

import org.vaadin.artur.jobschedulerui.ui.util.DateUtil;


public class DateTimeString extends Date {

	public DateTimeString(Date time) {
		super();

		if (time == null) {
			setTime(0);
		} else {
			setTime(time.getTime());
		}
	}

	@Override
	public String toString() {
		return DateUtil.formatDateTime(this);
	}
}
