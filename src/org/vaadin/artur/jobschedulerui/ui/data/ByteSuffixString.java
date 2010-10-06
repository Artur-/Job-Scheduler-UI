package org.vaadin.artur.jobschedulerui.ui.data;

public class ByteSuffixString implements Comparable<ByteSuffixString> {

	private Integer bytes;
	private final static String[] unitPrefix = new String[] { "", "K", "M",
			"G", "T", "P" };

	public ByteSuffixString(Integer bytes) {
		this.bytes = bytes;
	}

	@Override
	public String toString() {
		if (bytes == null) {
			return "";
		}

		int unitPrefixId = 0;
		int value = bytes;
		while (value > 1024) {
			value /= 1024;
			unitPrefixId++;
		}

		return value + " " + unitPrefix[unitPrefixId] + "B";
	}

	@Override
	public int compareTo(ByteSuffixString o) {
		if (o == null) {
			return this == null ? 0 : 1;
		}

		if (bytes == null) {
			return o.bytes == null ? 0 : 1;
		}
		return bytes.compareTo(o.bytes);
	}

}
