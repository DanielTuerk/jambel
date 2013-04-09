package com.jambit.jambel.hub.retrieval;

import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.hub.jobs.JobState;

import java.io.IOException;

public interface JobStateRetriever {

	JobState retrieve(Job job) throws IOException;

}
