package com.jambit.jambel.config;

import com.jambit.jambel.config.jambel.JambelConfiguration;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface ConfigListener {

    public void jambelCreated(JambelConfiguration jambelConfiguration);
    public void jambelRemoved(JambelConfiguration jambelConfiguration);
    public void jambelUpdated(JambelConfiguration jambelConfiguration);
}
