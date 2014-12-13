package com.zxsoft.crawler.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zxsoft.crawler.api.CrawlTool;
import com.zxsoft.crawler.api.JobCode;
import com.zxsoft.crawler.api.JobManager;
import com.zxsoft.crawler.api.JobStatus;
import com.zxsoft.crawler.api.JobStatus.State;
import com.zxsoft.crawler.util.ReflectionUtils;

public class RAMJobManager implements JobManager {
	int CAPACITY = 100;
	ThreadPoolExecutor exec = new MyPoolExecutor(10, CAPACITY, 1, TimeUnit.HOURS,
	        new ArrayBlockingQueue<Runnable>(CAPACITY));

	private class MyPoolExecutor extends ThreadPoolExecutor {

		public MyPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
		        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			synchronized (jobRunning) {
				jobRunning.offer(((JobWorker) r).jobStatus);
			}
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			synchronized (jobRunning) {
				jobRunning.remove(((JobWorker) r).jobStatus);
			}
			JobStatus status = ((JobWorker) r).jobStatus;
			synchronized (jobHistory) {
				if (!jobHistory.offer(status)) {
					jobHistory.poll();
					jobHistory.add(status);
				}
			}
		}
	}

	ArrayBlockingQueue<JobStatus> jobHistory = new ArrayBlockingQueue<JobStatus>(CAPACITY);
	ArrayBlockingQueue<JobStatus> jobRunning = new ArrayBlockingQueue<JobStatus>(CAPACITY);

	private static Map<JobType, Class<? extends CrawlTool>> typeToClass = new HashMap<JobType, Class<? extends CrawlTool>>();

	static {
		typeToClass.put(JobType.NETWORK_INSPECT, NetworkInspectJob.class);
		typeToClass.put(JobType.NETWORK_SEARCH, NetworkSearchJob.class);
	}
	
	private void addFinishedStatus(JobStatus status) {
		synchronized (jobHistory) {
			if (!jobHistory.offer(status)) {
				jobHistory.poll();
				jobHistory.add(status);
			}
		}
	}

	@Override
	@SuppressWarnings("fallthrough")
	public List<JobStatus> list(String crawlId, State state) throws Exception {
		List<JobStatus> res = new ArrayList<JobStatus>();
		if (state == null)
			state = State.ANY;
		switch (state) {
		case FINISHED: 
			res.addAll(jobHistory);
			break;
		case ANY:
			res.addAll(jobHistory);
			/* FALLTHROUGH */
		case RUNNING:
		case IDLE:
			res.addAll(jobRunning);
			break;
		default:
			res.addAll(jobHistory);
		}
		return res;
	}

	@Override
	public Map<String, Object> list() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("runningNum", jobRunning.size());
		map.put("historyNum", jobHistory.size());
		return map;
	}
	
	
	@Override
	public JobStatus get(String crawlId, String jobId) throws Exception {
		for (JobStatus job : jobRunning) {
			if (job.id.equals(jobId)) {
				return job;
			}
		}
		for (JobStatus job : jobHistory) {
			if (job.id.equals(jobId)) {
				return job;
			}
		}
		return null;
	}

	@Override
	public JobCode create(JobType jobType, Map<String, Object> args)
	        throws Exception {
		if (args == null)
			return new JobCode(52, "parameters not support");
		JobWorker worker = new JobWorker(jobType, args);
		exec.execute(worker);
		exec.purge();
		return new JobCode(23, "create job success");
	}

	@Override
	public boolean abort(String crawlId, String id) throws Exception {
		// find running job
		for (JobStatus job : jobRunning) {
			if (job.id.equals(id)) {
				job.state = State.KILLING;
				boolean res = job.tool.killJob();
				job.state = State.KILLED;
				return res;
			}
		}
		return false;
	}

	@Override
	public boolean stop(String crawlId, String id) throws Exception {
		// find running job
		for (JobStatus job : jobRunning) {
			if (job.id.equals(id)) {
				job.state = State.STOPPING;
				boolean res = job.tool.stopJob();
				return res;
			}
		}
		return false;
	}

	private class JobWorker implements Runnable {
		String id;
		JobType jobType;
		CrawlTool tool;
		Map<String, Object> args;
		JobStatus jobStatus;

		@SuppressWarnings("unchecked")
		JobWorker(JobType jobType, Map<String, Object> args)
		        throws Exception {
			this.id = jobType + "-" + hashCode();
			this.jobType = jobType;
			this.args = args;
			Class<? extends CrawlTool> clz = typeToClass.get(jobType);
			if (clz == null) {
				Class<?> c = Class.forName((String) args.get("class"));
				if (c instanceof Class) {
					clz = (Class<? extends CrawlTool>) c;
				}
			}
			
			tool = ReflectionUtils.newInstance(clz);
			
			jobStatus = new JobStatus(id, jobType, (String)args.get("comment"), args, State.IDLE, "idle");
			jobStatus.tool = tool;
		}

		@Override
		public void run() {
			try {
				jobStatus.state = State.RUNNING;
				jobStatus.msg = "OK";
				jobStatus.result = tool.run(args);
				jobStatus.state = State.FINISHED;
			} catch (Exception e) {
				e.printStackTrace();
				jobStatus.msg = "ERROR: " + e.toString();
				jobStatus.state = State.FAILED;
			}
		}
	}
}
