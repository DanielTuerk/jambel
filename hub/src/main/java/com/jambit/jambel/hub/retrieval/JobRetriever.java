package com.jambit.jambel.hub.retrieval;

import com.jambit.jambel.hub.jobs.Job;

import java.io.IOException;
import java.net.URL;

public interface JobRetriever {

	Job retrieve(URL jobUrl) throws IOException;

}
