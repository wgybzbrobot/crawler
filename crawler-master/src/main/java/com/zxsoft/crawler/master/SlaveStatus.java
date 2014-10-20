package com.zxsoft.crawler.master;

import java.io.Serializable;
import com.zxsoft.crawler.api.Machine;

public class SlaveStatus implements Serializable, Comparable<SlaveStatus> {

	private static final long serialVersionUID = -5082175090150630835L;

	public static enum State {
		IDLE, RUNNING, STOP
	};

	public int runningNum;
	public int historyNum;

	public int code;
	public String msg;
	public State state;

	public Machine machine;
	public float score;

	public SlaveStatus() {
	}

	public SlaveStatus(Machine machine, /* String slaveId, */int runningNum, int historyNum,
	        int code, String msg, State state/* , String slaveComment */) {
		super();
		this.machine = machine;
		// this.slaveId = slaveId;
		this.runningNum = runningNum;
		this.historyNum = historyNum;
		this.code = code;
		this.msg = msg;
		this.state = state;
		// this.slaveComment = slaveComment;
	}

	public SlaveStatus(Machine machine,/* String slaveId, */int runningNum, int historyNum) {
		super();
		// this.slaveId = slaveId;
		this.machine = machine;
		this.runningNum = runningNum;
		this.historyNum = historyNum;
	}

	public String toString() {
		return machine.getId();
	}

	@Override
	public boolean equals(Object obj) {
		SlaveStatus stat = (SlaveStatus) obj;

		if (this.machine.getId().equals(stat.machine.getId())) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(SlaveStatus o) {
		if (this.machine.getId().equals(o.machine.getId()))
			return 0;
//		else if (this.score == o.score)
//			return 0;
//		else
			return -1;
	}
}
