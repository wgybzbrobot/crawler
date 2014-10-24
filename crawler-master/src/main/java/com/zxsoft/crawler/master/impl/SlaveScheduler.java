package com.zxsoft.crawler.master.impl;

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlaveScheduler {
	
	private static Logger LOG = LoggerFactory.getLogger(SlaveScheduler.class);
	
	private PriorityBlockingQueue<ScoredMachine> queue = new PriorityBlockingQueue<ScoredMachine>(20);
	
	private static SlaveScheduler slaveScheduler;
	
	private SlaveScheduler() {}
	
	public synchronized static SlaveScheduler getInstance() {
		if (slaveScheduler == null) {
			slaveScheduler = new SlaveScheduler();
		}
		return slaveScheduler;
	}
	
	public ScoredMachine selectSlave() {
		if (queue == null || queue.isEmpty())
			return null;
		ScoredMachine sm = queue.poll();
		if (sm != null) { // 重新计算score
			sm.runningCount += 1;
			sm.score = 1.0f / (1.0f + sm.runningCount);
			queue.put(sm);
		}
		
		return sm;
	}
	
	public void addSlave(ScoredMachine sm) {
		if (queue.contains(sm)) queue.remove(sm);
		queue.add(sm);
		LOG.debug(sm.machine.getId() + ": " + sm.score);
	}

	public String descibe() {
		StringBuilder sb = new StringBuilder();
		Iterator<ScoredMachine> iterator = queue.iterator();
		while (iterator.hasNext()) {
			ScoredMachine sm = iterator.next();
			sb.append("[" + sm.machine.getId() + ": " +  sm.score + "]\n");
		}
		return sb.toString();
	}
	
}
