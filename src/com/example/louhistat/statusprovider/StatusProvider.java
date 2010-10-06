package com.example.louhistat.statusprovider;

import java.util.List;

import com.example.louhistat.server.data.QueueInfo;

public interface StatusProvider {

	String getAvailableQueuesCommand();

	List<QueueInfo> parseAvailableQueues(String commandResult);
}
