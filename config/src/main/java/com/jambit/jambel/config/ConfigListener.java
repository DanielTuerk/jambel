package com.jambit.jambel.config;

import com.jambit.jambel.config.jambel.JambelConfiguration;

import java.nio.file.Path;

/**
 * Listener for the jambel configurations.
 * The interface is called by modification events on the JSON configuration files.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface ConfigListener {

    public void jambelCreated(Path path, JambelConfiguration jambelConfiguration);
    public void jambelRemoved(Path path);
    public void jambelUpdated(Path path, JambelConfiguration jambelConfiguration);
}
