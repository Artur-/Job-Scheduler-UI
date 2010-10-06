package org.vaadin.artur.jobschedulerui.ui.data;

import java.text.NumberFormat;
import java.util.Locale;

public class FormattedInteger implements Comparable<FormattedInteger> {

	// FIXME: Locale from where?
	private static final NumberFormat FORMATTER = NumberFormat
			.getIntegerInstance(new Locale("fi", "FI"));
	private Integer value;

	public FormattedInteger(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (value == null) {
			return "";
		}
		return FORMATTER.format(value);
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public int compareTo(FormattedInteger o) {
		if (o == null) {
			return this == null ? 0 : 1;
		}
		if (o.value == null) {
			return value == null ? 0 : 1;
		}
		if (value == null) {
			return o.value == null ? 0 : -1;
		}

		return value.compareTo(o.value);

	}

}
