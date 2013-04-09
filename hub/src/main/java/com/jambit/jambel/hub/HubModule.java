package com.jambit.jambel.hub;

import org.springframework.stereotype.Component;

@Component
public class HubModule  {

	public final static int POLLING_THREADS = 5;


	protected void configure() {
		// retrieving
//		bind(JobRetriever.class).to(JenkinsRetriever.class);
//		bind(JobStateRetriever.class).to(JenkinsRetriever.class);
//
//		// hub
//		bind(JobStatusReceiver.class).to(JobStatusHub.class);
//
//		// polling
//		bind(JobStatePoller.class);
//		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("poller-%d").build();
//		bind(ScheduledExecutorService.class).annotatedWith(Names.named("poller")).toInstance(
//				Executors.newScheduledThreadPool(POLLING_THREADS, namedThreadFactory));
	}

}
