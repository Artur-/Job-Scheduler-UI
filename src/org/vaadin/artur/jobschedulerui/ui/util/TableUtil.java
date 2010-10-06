package org.vaadin.artur.jobschedulerui.ui.util;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

public class TableUtil {

	public static void showWaitText(Table table) {
		Object sync = table.getApplication();
		if (sync == null) {
			// Table not yet attached
			sync = new Object();
		}
		synchronized (sync) {
			table.setContainerDataSource(new IndexedContainer());
			table.addContainerProperty("wait", String.class,
					" - - Fetching data, please wait - -");
			table.setColumnAlignment("wait", Table.ALIGN_CENTER);
			table.setColumnHeader("wait", "");
			table.addItem();
		}
	}

	public static void showErrorMessage(Table table, String message, Throwable e) {
		synchronized (table.getApplication()) {
			IndexedContainer c = new IndexedContainer();
			c.addContainerProperty("Error", String.class,
					message + ": " + e.getMessage());
			c.addItem();
			table.setContainerDataSource(c);
		}

	}

}
