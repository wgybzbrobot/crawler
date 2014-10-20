package com.zxsoft.crawler.master.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger LOG = LoggerFactory.getLogger(RAMSlaveManager.class);
	
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
	
	private static SlaveScheduler scheduler = SlaveScheduler.getInstance();
	
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
		for (Future<SlaveStatus> future : futures) {
			SlaveStatus status = future.get();
			if (status.state == State.STOP)  {
				status.score = 0.0f;
			} else {
				status.score = 1.0f / (1.0f + status.runningNum);
				
				ScoredMachine sm = new ScoredMachine(status.machine, status.runningNum, status.score);
				scheduler.addSlave(sm);
				
				LOG.info(status.machine.getId() + ":" + status.score);
			}
			res.add(status);
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
			
			Client cli = new Client(Protocol.HTTP);
			cli.setConnectTimeout(1000);
			
			ClientResource client = new ClientResource(new Context(), url);
			
			client.setNext(cli);
			client.setRetryAttempts(0);
			client.getContext().getParameters().add("socketTimeout",String.valueOf(1000));
			
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
				((Client)client.getNext()).stop();
			}
			return slaveStatus;
		}
	}

	class NumNum {
		int runningNum;
		int historyNum;
	}
	
	
	/**
	 *  choose a url to send job
	 */
	public String chooseUrl() {
		ScoredMachine sm = scheduler.selectSlave();
		
		URL url = null;
		try {
	        url = new URL("http", sm.machine.getIp(), sm.machine.getPort(), "/" + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH);
        } catch (MalformedURLException e) {
	        e.printStackTrace();
        }
		return url.toExternalForm();
//		return  "http://localhost:8989/" + SlavePath.PATH + "/" + SlavePath.JOB_RESOURCE_PATH;
	}
	
	@Override
	public String create(final Map<String, Object> args) throws Exception {
		
		final String url = chooseUrl();
		Thread t = new Thread() {
			public void run() {
				ClientResource client = new ClientResource(url);
				Representation r = client.put(args);
				try {
	                String text = r.getText();
	                LOG.info(text);
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
