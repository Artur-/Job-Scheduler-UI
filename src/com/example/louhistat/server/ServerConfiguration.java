package com.example.louhistat.server;

import com.example.louhistat.LouhistatApplication;

public class ServerConfiguration {

	private String hostName;

	public String getHostName() {
		return hostName;
	}

	public static ServerConfiguration get() {
		return LouhistatApplication.get().getServerConfiguration();
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

}
