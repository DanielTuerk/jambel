package com.jambit.jambel;

import com.google.common.net.HostAndPort;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.hub.init.JobInitializer;
import com.jambit.jambel.light.SignalLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JambelInitializer {

	private static final Logger logger = LoggerFactory.getLogger(JambelInitializer.class);

	private final JobStatusHub hub;

	private final JobInitializer jobInitializer;

	private final SignalLight signalLight;

	public JambelInitializer(JobStatusHub hub, JobInitializer jobInitializer, SignalLight signalLight) {
		this.hub = hub;
		this.jobInitializer = jobInitializer;
		this.signalLight = signalLight;
	}

	public void init() {
		testSignalLightConnection();

		initHub();

        //TODO
		logger.info(
				"Jambel is ready to receive notifications. Be sure to configure Jenkins Notifications plugin (https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin) for each job to HTTP POST to http://<HOSTNAME>:{}{}",
				8080, "foo");
	}

	private void testSignalLightConnection() {
		SignalLightConfiguration configuration = signalLight.getConfiguration();

		HostAndPort hostAndPort = configuration.getHostAndPort();
		if (signalLight.isAvailable()) {
			logger.info("signal light is available at {}", hostAndPort);
		}
		else {
			logger.warn("signal light is not available at {}", hostAndPort);
		}
	}

	private void initHub() {
		jobInitializer.initJobs();
		hub.updateSignalLight();
	}


    public JobInitializer getJobInitializer() {
        return jobInitializer;
    }

    public SignalLight getSignalLight() {
        return signalLight;
    }

    public JobStatusHub getHub() {
        return hub;
    }
}