package com.jambit.jambel.config.jambel;

import java.util.Collection;

/**
 * Configuration container for an {@link SignalLightConfiguration} with jenkins jobs.
 */
public final class JambelConfiguration {

	private Collection<JobConfiguration> jobs;

	private SignalLightConfiguration signalLight;

	private int httpPort;

	public Collection<JobConfiguration> getJobs() {
		return jobs;
	}

	public SignalLightConfiguration getSignalLightConfiguration() {
		return signalLight;
	}

	public int getHttpPort() {
		return httpPort;
	}

}
