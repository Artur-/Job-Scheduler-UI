package org.vaadin.artur.jobschedulerui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RefreshCheckerThread extends Thread {
	private int refreshInterval = 60;
	private Date lastRefreshed = null;
	private List<RefreshListener> listeners = new ArrayList<RefreshCheckerThread.RefreshListener>();
	private boolean terminated = false;

	public interface RefreshEvent {

	}

	public interface RefreshListener {
		public void doRefresh(RefreshEvent event);
	}

	public RefreshCheckerThread() {
	}

	public void addListener(RefreshListener listener) {
		listeners.add(listener);

	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;

	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	@Override
	public void run() {
		reset();

		while (true) {
			delay(1000);
			if (terminated) {
				break;
			}
			if (timeSinceLastRefresh() > refreshInterval) {
				lastRefreshed = new Date();
				fireRefreshEvent();
			}
		}

	}

	private void delay(int i) {
		try {
			sleep(i);
		} catch (InterruptedException e) {
		}

	}

	private void fireRefreshEvent() {
		Object[] ls = listeners.toArray();

		for (Object o : ls) {
			RefreshListener l = (RefreshListener) o;
			l.doRefresh(new RefreshEvent() {
			});
		}

	}

	/**
	 * Time in seconds since last refresh event.
	 * 
	 * @return
	 */
	private int timeSinceLastRefresh() {
		Date now = new Date();
		long diff = now.getTime() - lastRefreshed.getTime();
		return (int) (diff / 1000);
	}

	public void terminate() {
		terminated = true;

	}

	public void reset() {
		lastRefreshed = new Date();

	}
}
