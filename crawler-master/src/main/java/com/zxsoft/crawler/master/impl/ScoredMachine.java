package com.zxsoft.crawler.master.impl;

import com.zxsoft.crawler.api.Machine;

/**
 * 描述评分的slave机器节点
 * @author xiayun
 *
 */
public class ScoredMachine implements Comparable<ScoredMachine>{
	protected float score;
	protected int runningCount;
	protected Machine machine;
	public ScoredMachine(Machine machine, int runningCount, float score) {
		this.machine = machine;
		this.runningCount = runningCount;
		this.score = score;
    }
	@Override
    public int compareTo(ScoredMachine o) {
        if (score > o.score) return -1;
        if (score < o.score) return 1;
        return 0;
    }
	@Override
	public boolean equals(Object obj) {
		ScoredMachine foo = (ScoredMachine)obj;
		if (foo.machine.getId().equals(machine.getId()))
			return true;
	    return false;
	}
} 