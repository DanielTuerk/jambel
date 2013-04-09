package com.jambit.jambel.hub.poster;

import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.hub.JobStatusHub;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface JobStateReceiverRegistry {
    void register(JambelConfiguration jambelConfiguration, JobStatusHub hub);
    void unRegister(JambelConfiguration jambelConfiguration);
}
