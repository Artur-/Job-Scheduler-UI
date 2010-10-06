package com.example.louhistat.statusprovider;

import java.util.ArrayList;
import java.util.List;

import com.example.louhistat.server.data.QueueInfo;

public class CrayXT4StatusProvider implements StatusProvider {

	private static final String QSTAT_LIST_QUEUES = "qstat -Q -f";

	@Override
	public String getAvailableQueuesCommand() {
		return QSTAT_LIST_QUEUES;
	}

	@Override
	public List<QueueInfo> parseAvailableQueues(String commandResult) {
		List<QueueInfo> queueInfos = new ArrayList<QueueInfo>();

		// System.err.println("Queues: " + commandResult);
		String[] queues = commandResult.split("Queue:");
		for (String queue : queues) {
			if (queue.length() == 0) {
				continue;
			}
			String[] rows = queue.trim().split("[\r\n]+");

			String queueName = rows[0];
			QueueInfo queueInfo = new QueueInfo(queueName);

			String continuation = null;

			for (int i = rows.length - 1; i >= 1; i--) {
				String row = rows[i];
				if (continuation != null) {
					row = row + continuation;
				}

				if (row.startsWith("        ") || row.startsWith("\t")) {
					// This is a continuation of the previous row
					continuation = row.trim();
					continue;
				} else {
					continuation = null;
				}

				String[] data = row.split("=");
				if (data.length != 2) {
					System.err.println("invalid data: " + row);
					continue;
				}
				String param = data[0].trim();
				String value = data[1].trim();
				queueInfo.setParameter(param, value);
			}

			queueInfos.add(queueInfo);
		}

		return queueInfos;
	}

}
