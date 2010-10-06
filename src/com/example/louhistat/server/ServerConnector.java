package com.example.louhistat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.example.louhistat.LouhistatApplication;
import com.example.louhistat.server.data.QueueInfo;
import com.example.louhistat.statusprovider.CrayXT4StatusProvider;
import com.example.louhistat.statusprovider.StatusProvider;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

public class ServerConnector {

	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	private static final int TIMEOUT_IN_MS = 10000;

	private JSch jsch = new JSch();
	private Session activeSession = null;

	private String hostname;
	private Credentials credentials;
	private StatusProvider statusProvider = new CrayXT4StatusProvider();

	static {
		JSch.setLogger(new Logger() {
			public boolean isEnabled(int level) {
				return true;
			}

			public void log(int level, String message) {
				System.err.println(message);
			}
		});
	}

	public boolean verifyCredentials() {
		try {
			getSession();
			return true;
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private Session getSession() throws JSchException {
		if (activeSession != null) {
			if (activeSession.isConnected()) {
				return activeSession;
			}

			// FIXME: Is this necessary as it is "not connected"?
			activeSession.disconnect();
		}

		Session session = jsch.getSession(credentials.getUsername(), hostname,
				22);
		session.setConfig(STRICT_HOST_KEY_CHECKING, "no");
		session.setPassword(credentials.getPassword());
		// session.setUserInfo(credentials);
		session.connect(TIMEOUT_IN_MS);
		return session;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public static ServerConnector get() {
		return LouhistatApplication.get().getServerConnector();
	}

	public List<QueueInfo> getAvailableQueues() throws JSchException {
		ChannelExec shell = (ChannelExec) getSession().openChannel("exec");
		shell.setCommand(statusProvider.getAvailableQueuesCommand());
		shell.setInputStream(null);
		final StringBuilder textResult = new StringBuilder();

		shell.setOutputStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				textResult.append((char) b);
			}
		});
		shell.connect();
		while (!shell.isClosed()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		System.out.println("Done. Got " + textResult.length() + " bytes");
		return statusProvider.parseAvailableQueues(textResult.toString());
	}

	public String getUserName() {
		return credentials.getUsername();
	}
}
