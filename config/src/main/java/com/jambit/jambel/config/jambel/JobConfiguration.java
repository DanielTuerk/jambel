package com.jambit.jambel.config.jambel;

import com.google.common.base.Optional;

import java.net.URL;

public class JobConfiguration {

	private URL jenkinsJobUrl;

	private UpdateMode updateMode;

	private Integer pollingInterval;

    private boolean initialJobStatePoll;

	public URL getJenkinsJobUrl() {
		return jenkinsJobUrl;
	}

	public UpdateMode getUpdateMode() {
		return updateMode;
	}

	public Optional<Integer> getPollingInterval() {
		return Optional.fromNullable(pollingInterval);
	}

    public boolean isInitialJobStatePoll() {
        return initialJobStatePoll;
    }
}
