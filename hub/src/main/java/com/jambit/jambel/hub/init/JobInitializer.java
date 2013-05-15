package com.jambit.jambel.hub.init;

import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.config.jambel.JobConfiguration;
import com.jambit.jambel.config.jambel.UpdateMode;
import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.hub.jobs.JobState;
import com.jambit.jambel.hub.poller.JobStatePoller;
import com.jambit.jambel.hub.poster.JobStateReceiverRegistry;
import com.jambit.jambel.hub.retrieval.JobRetriever;
import com.jambit.jambel.hub.retrieval.JobStateRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;

public class JobInitializer {

    private static final Logger logger = LoggerFactory.getLogger(JobInitializer.class.getName());

    private final JobStatusHub hub;

    private final JobStatePoller poller;

    private final JambelConfiguration jambelConfiguration;

    private final JobRetriever jobRetriever;


    private final JobStateRetriever jobStateRetriever;

    @Autowired
    private JobStateReceiverRegistry jobStateReceiverRegistry;

    public JobInitializer(JobStatusHub hub, JambelConfiguration jambelConfiguration,
            JobRetriever jobRetriever, JobStateRetriever jobStateRetriever, ScheduledExecutorService pollerExecutor,
            JobStateReceiverRegistry jobStateReceiverRegistry) {
        this.hub = hub;
        this.poller = new JobStatePoller(pollerExecutor, jobStateRetriever, hub);
        this.jambelConfiguration = jambelConfiguration;
        this.jobRetriever = jobRetriever;
        this.jobStateRetriever = jobStateRetriever;
        this.jobStateReceiverRegistry = jobStateReceiverRegistry;
    }

    /**
     */
    public void initJobs() {
        for (JobConfiguration jobConfig : jambelConfiguration.getJobs()) {
            URL jobUrl = jobConfig.getJenkinsJobUrl();
            Job job = null;
            JobState state = null;
            if (jobConfig.isInitialJobStatePoll()) {
                try {
                    job = jobRetriever.retrieve(jobUrl);
                    state = jobStateRetriever.retrieve(job);
                } catch (IOException e) {
                    logger.warn("could not initial retrieve job or its last build status at {}",
                            jobUrl, e);
                }
            }
            if (job == null || state == null) {
                job = new Job(jobUrl.getPath().split("/")[2], jobUrl.toString());
                // load from storage
                state = hub.getLastStateStorage().loadLastState(job);
            }
            hub.addJob(job, state);
            logger.info("initialized job '{}' with state '{}'", new Object[]{job, state});


            UpdateMode updateMode = jobConfig.getUpdateMode();
            switch (updateMode) {
                case polling:
                    poller.addPollingTask(job, jobConfig.getPollingInterval());
                    break;
                case posting:
                    jobStateReceiverRegistry.subscribe(jambelConfiguration, hub);
                    break;
            }
        }
    }

    public JambelConfiguration getJambelConfiguration() {
        return jambelConfiguration;
    }
}
