package org.vaadin.artur.jobschedulerui.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.vaadin.artur.jobschedulerui.JobSchedulerUIApplication;
import org.vaadin.artur.jobschedulerui.server.data.JobInfo;
import org.vaadin.artur.jobschedulerui.server.data.QueueInfo;
import org.vaadin.artur.jobschedulerui.statusprovider.DummyStatusProvider;
import org.vaadin.artur.jobschedulerui.statusprovider.StatusProvider;

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
	private StatusProvider statusProvider;

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
		if (statusProvider instanceof DummyStatusProvider) {
			// Always ok for dummy ...
			return true;
		}

		try {
			// Try to log in and leave the session open
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

		activeSession = session;
		return session;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setStatusProvider(StatusProvider statusProvider) {
		this.statusProvider = statusProvider;
	}

	public static ServerConnector get() {
		return JobSchedulerUIApplication.get().getServerConnector();
	}

	public String getUserName() {
		return credentials.getUsername();
	}

	public List<QueueInfo> getAvailableQueues() throws JSchException {
		String command = statusProvider.getAvailableQueuesCommand();
		String result = "";
		if (command != null) {
			result = exec(command);
		}

		return statusProvider.parseQueues(result);
	}

	public List<JobInfo> getJobs() throws JSchException {
		String command = statusProvider.getListJobsCommand();
		String result = "";
		if (command != null) {
			result = exec(command);
		}

		return statusProvider.parseJobs(result);
	}

	private synchronized String exec(String command) throws JSchException {
		ChannelExec shell = (ChannelExec) getSession().openChannel("exec");
		try {
			shell.setCommand(command);
			shell.setInputStream(null);
			final StringBuilder textResult = new StringBuilder();

			shell.setOutputStream(new OutputStream() {

				@Override
				public void write(int b) throws IOException {
					textResult.append((char) b);
				}
			});
			try {
				shell.setExtOutputStream(shell.getOutputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			shell.connect();
			while (!shell.isClosed()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			System.out.println("Command '" + command + "' done. Got "
					+ textResult.length() + " bytes");
			return textResult.toString();
		} finally {
			shell.disconnect();
		}

	}

	public String getHostName() {
		return hostname;
	}

	public void cleanup() {
		if (activeSession != null) {
			activeSession.disconnect();
			activeSession = null;
		}

	}

}
