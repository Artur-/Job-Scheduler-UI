package com.example.louhistat.ui.data;

public class ByteSuffixString {

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

}
