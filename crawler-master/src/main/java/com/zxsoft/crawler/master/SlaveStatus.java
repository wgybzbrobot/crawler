package com.zxsoft.crawler.master;

import java.io.Serializable;


public class SlaveStatus implements Serializable {
	
    private static final long serialVersionUID = -5082175090150630835L;

	public static enum State {
		IDLE, RUNNING, STOP
	};

	public String slaveId;
	public int runningNum;
	public int historyNum;
	
	public int code;
	public String msg;
	public State state;
	
	public SlaveStatus() {}

	
	
	public SlaveStatus(String slaveId, int runningNum, int historyNum, int code, String msg,
            State state) {
	    super();
	    this.slaveId = slaveId;
	    this.runningNum = runningNum;
	    this.historyNum = historyNum;
	    this.code = code;
	    this.msg = msg;
	    this.state = state;
    }



	public SlaveStatus(String slaveId, int runningNum, int historyNum) {
	    super();
	    this.slaveId = slaveId;
	    this.runningNum = runningNum;
	    this.historyNum = historyNum;
    }
	
	

}
