package com.zxsoft.crawler.master.impl;

import org.junit.Test;

import com.zxsoft.crawler.api.Machine;

public class TestSlaveScheduler {
	
	SlaveScheduler scheduler = SlaveScheduler.getInstance();

//	@Test
//	public void testAddAndSelectSlave() {
//		
//		int i = 0;
//		while (i++ < 100) {
//			Machine machine =  new Machine("machine" + i, "192.168.4.137", 8000 + i, "Machine " + i);
//			ScoredMachine sm = new ScoredMachine(machine, 1, 0.5f);
//			scheduler.addSlave(sm);
//		}
//		
//		while (i-- > 0) {
//			ScoredMachine sm = scheduler.selectSlave();
//			System.out.println(sm.machine.getId() + ": " + sm.score);
//		}
//		
//		System.out.println(scheduler.descibe());
//	}
}
