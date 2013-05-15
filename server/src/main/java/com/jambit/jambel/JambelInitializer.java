package com.jambit.jambel;

import com.google.common.net.HostAndPort;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.hub.init.JobInitializer;
import com.jambit.jambel.light.SignalLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The initializer loading and configure a single {@link SignalLight} for the system.
 * It perform all initialization steps of the components which are required by the {@link SignalLight}.
 * <p/>
 * Each {@link SignalLight} is after ready after calling {@see #init}.
 * To stop and destroy the loaded {@link SignalLight} the {@link JambelDestroyer} must been used.
 *
 * @author frampp
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class JambelInitializer {

    private static final Logger logger = LoggerFactory.getLogger(JambelInitializer.class);

    private final JobStatusHub hub;

    private final JobInitializer jobInitializer;

    private final SignalLight signalLight;

    /**
     * Create a new instance to initialize an {@link SignalLight}.
     *
     * @param hub            {@link JobStatusHub} loading the state of the {@link SignalLight} by the configured {@link com.jambit.jambel.hub.jobs.Job}s
     * @param jobInitializer {@link JobInitializer} load the jobs of the {@link SignalLight}
     * @param signalLight    {@link SignalLight} to initialize
     */
    public JambelInitializer(JobStatusHub hub, JobInitializer jobInitializer, SignalLight signalLight) {
        this.hub = hub;
        this.jobInitializer = jobInitializer;
        this.signalLight = signalLight;
    }

    /**
     * Start the initialization process for the component.
     */
    public void init() {
        testSignalLightConnection();
        initHub();
        logger.info("Jambel is ready to receive notifications. Be sure to configure Jenkins Notifications plugin " +
                "(https://wiki.jenkins-ci.org/display/JENKINS/Notification+Plugin) for each job to HTTP POST to the jambel server");
    }

    /**
     * Perform a simple connection test and log the result.
     */
    private void testSignalLightConnection() {
        SignalLightConfiguration configuration = signalLight.getConfiguration();

        HostAndPort hostAndPort = configuration.getHostAndPort();
        if (signalLight.isAvailable()) {
            logger.info("signal light is available at {}", hostAndPort);
        } else {
            logger.warn("signal light is not available at {}", hostAndPort);
        }
    }

    /**
     * Initialize the {@link JobStatusHub} and update the status of the {@link SignalLight}.
     */
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