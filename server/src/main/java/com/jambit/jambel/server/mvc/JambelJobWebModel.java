package com.jambit.jambel.server.mvc;

import com.jambit.jambel.config.jambel.JobConfiguration;
import com.jambit.jambel.hub.jobs.JobState;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class JambelJobWebModel {
    private final JobConfiguration jobConfiguration;
    private final JobState jobState;

    public JambelJobWebModel(JobConfiguration jobConfiguration, JobState jobState) {
        this.jobConfiguration = jobConfiguration;
        this.jobState = jobState;
    }

    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }

    public JobState getJobState() {
        return jobState;
    }
}
