package com.jambit.jambel.server.mvc;

import com.google.common.collect.Lists;
import com.jambit.jambel.light.SignalLightStatus;

import java.util.List;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class JambelWebModel {

    private final String host;
    private final int port;
    private final SignalLightStatus status;
    private final List<JambelJobWebModel> jobsConfiguration = Lists.newArrayList();

    public JambelWebModel(String host, int port, SignalLightStatus status) {
        this.host = host;
        this.port = port;
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SignalLightStatus getStatus() {
        return status;
    }

    public List<JambelJobWebModel> getJobsConfiguration() {
        return jobsConfiguration;
    }
}
