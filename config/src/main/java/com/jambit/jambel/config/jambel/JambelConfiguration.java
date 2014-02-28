package com.jambit.jambel.config.jambel;

import java.util.Collection;
import java.util.Collections;

/**
 * Configuration container for an {@link SignalLightConfiguration} with jenkins
 * jobs.
 */
public final class JambelConfiguration {

	private Collection<JobConfiguration> jobs;

	private SignalLightConfiguration signalLight;

	public JambelConfiguration() {

	}

	public JambelConfiguration(SignalLightConfiguration signalLightConfiguration) {
		this.signalLight = signalLightConfiguration;
		this.jobs=Collections.emptyList();
	}

	public Collection<JobConfiguration> getJobs() {
		return jobs;
	}

	public SignalLightConfiguration getSignalLightConfiguration() {
		return signalLight;
	}

}
