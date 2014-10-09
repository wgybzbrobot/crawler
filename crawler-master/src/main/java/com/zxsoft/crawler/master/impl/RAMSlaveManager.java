package com.zxsoft.crawler.master.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;

import com.google.gson.Gson;
import com.zxsoft.crawler.api.Machine;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.master.SlaveCache;
import com.zxsoft.crawler.master.SlaveManager;
import com.zxsoft.crawler.master.SlaveStatus;
import com.zxsoft.crawler.master.SlaveStatus.State;
import com.zxsoft.crawler.slave.SlavePath;
import com.zxsoft.crawler.storage.WebPage.JOB_TYPE;

public class RAMSlaveManager implements SlaveManager {
//	int CAPACITY = 100;
//	ThreadPoolExecutor exec = new MyPoolExecutor(10, CAPACITY, 1, TimeUnit.HOURS,
//	        new ArrayBlockingQueue<Runnable>(CAPACITY));

	private class MyPoolExecutor extends ThreadPoolExecutor {
		public MyPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
		        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}
	}

	public static Set<Machine> runningMachines = new HashSet<Machine>(100);
	
	static {
		List<Machine> list = SlaveCache.machines;
		if (CollectionUtils.isEmpty(list)) {
			throw new NullPointerException("No Slave machines found, please configure it.");
		}
		for (Machine machine : list) {
	        runningMachines.add(machine);
        }
	}
	
	@Override
	@SuppressWarnings("fallthrough")
	public List<SlaveStatus> list() throws Exception {
		List<SlaveStatus> res = new ArrayList<SlaveStatus>();

		List<Machine> machines = SlaveCache.machines;

		ThreadPoolExecutor exec = new MyPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
		        new ArrayBlockingQueue<Runnable>(100));
		List<Callable<SlaveStatus>> tasks = new ArrayList<Callable<SlaveStatus>>();
		for (Machine machine : machines) {
			String url = "http://" + machine.getIp() + ":" + machine.getPort() + "/" + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH;
			Vistor vistor = new Vistor(machine);
			tasks.add(vistor);
		}
		List<Future<SlaveStatus>> futures = exec.invokeAll(tasks);
		synchronized (runningMachines) {
	        
			for (Future<SlaveStatus> future : futures) {
				SlaveStatus status = future.get();
				if (status.state == State.STOP)  {
					runningMachines.remove(status.machine);
				} else {
					runningMachines.add(status.machine);
				}
				res.add(status);
			}
		}
		exec.shutdown();
		return res;
	}

	public void listRunning(String slaveId) {
		
	}
	
	class Vistor implements Callable<SlaveStatus> {
		private Machine machine;

		public Vistor(Machine machine) {
			this.machine = machine;
		}

		public SlaveStatus call() throws Exception {
			String url = "http://" + machine.getIp() + ":" + machine.getPort() + "/" + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH;
			ClientResource client = new ClientResource(url);
			Representation representation = null;
			SlaveStatus slaveStatus = null;
			try {
				representation = client.get();
				String text = representation.getText();
				NumNum tm = new Gson().fromJson(text, NumNum.class);
				slaveStatus = new SlaveStatus(machine, tm.runningNum,
				        tm.historyNum, 2000, "success", State.RUNNING);
			} catch (Exception e) {
				e.printStackTrace();
				slaveStatus = new SlaveStatus(machine, 0, 0, 5000, e.toString(), State.STOP);
			} finally {
				client.release();
			}
			return slaveStatus;
		}
	}

	class NumNum {
		int runningNum;
		int historyNum;
	}
	
	public String chooseUrl() {
		// choose a url to send job
		if (CollectionUtils.isEmpty(runningMachines)) {
			
		}
		return  "http://localhost:8989/" + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH;
	}
	
	@Override
	public String create(final Map<String, Object> args) throws Exception {
		
		final String url = chooseUrl();
		Thread t = new Thread() {
			public void run() {
				ClientResource client = new ClientResource(url);
				Representation r = client.put(args);
				try {
	                r.getText();
                } catch (IOException e) {
	                e.printStackTrace();
                } finally {
                	client.release();
                }
			}
		};
		t.start();
		
		return url;
	}

	@Override
	public boolean abort(String slaveId, String crawlId, String id) throws Exception {
		// find running job
		// for (SlaveStatus job : jobRunning) {
		// if (job.id.equals(id)) {
		// job.state = State.KILLING;
		// boolean res = job.tool.killJob();
		// job.state = State.KILLED;
		// return res;
		// }
		// }
		return false;
	}

	@Override
	public boolean stop(String slaveId, String crawlId, String id) throws Exception {
		// find running job
		// for (SlaveStatus job : jobRunning) {
		// if (job.id.equals(id)) {
		// job.state = State.STOPPING;
		// boolean res = job.tool.stopJob();
		// return res;
		// }
		// }
		return false;
	}

}
