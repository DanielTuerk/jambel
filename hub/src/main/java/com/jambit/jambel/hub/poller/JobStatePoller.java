package com.jambit.jambel.hub.poller;

import com.google.common.base.Optional;
import com.jambit.jambel.hub.JobStatusReceiver;
import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.hub.retrieval.JobStateRetriever;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobStatePoller {

	public final static int DEFAULT_POLLING_INTERVAL = 5000;

	private final ScheduledExecutorService executor;

	private final JobStateRetriever retriever;
	private final JobStatusReceiver receiver;

	public JobStatePoller(ScheduledExecutorService executor, JobStateRetriever retriever,
			JobStatusReceiver receiver) {
		this.executor = executor;
		this.retriever = retriever;
		this.receiver = receiver;
	}

	public void addPollingTask(Job job, Optional<Integer> pollingInterval) {
		int interval = pollingInterval.or(DEFAULT_POLLING_INTERVAL);
		//executor.scheduleAtFixedRate(new PollTask(job, retriever, receiver), interval, interval, TimeUnit.MILLISECONDS);
	}

}
