package com.jambit.jambel.hub.poster;

import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.hub.JobStatusHub;

/**
 * Registry to handle {@link JobStatusHub}s for receiving job states.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface JobStateReceiverRegistry {

    /**
     * Subscribe the {@link JobStatusHub} for the {@link JambelConfiguration}.
     *
     * @param jambelConfiguration {@link JambelConfiguration}
     * @param hub                 {@link JobStatusHub}
     */
    void subscribe(JambelConfiguration jambelConfiguration, JobStatusHub hub);

    /**
     * Unsubscribe the {@link JobStatusHub} for the {@link JambelConfiguration}.
     *
     * @param jambelConfiguration {@link JambelConfiguration}
     */
    void unsubscribe(JambelConfiguration jambelConfiguration);
}
