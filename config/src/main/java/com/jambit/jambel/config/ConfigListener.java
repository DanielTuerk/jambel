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

    /**
     * New {@link JambelConfiguration} is detected and loaded.
     *
     * @param path                {@link Path} file which was created
     * @param jambelConfiguration {@link JambelConfiguration} new configuration
     */
    public void jambelCreated(Path path, JambelConfiguration jambelConfiguration);

    /**
     * {@link JambelConfiguration} was removed from project.
     *
     * @param path {@link Path} file which was removed
     */
    public void jambelRemoved(Path path);

    /**
     * Changes in {@JambelConfiguration} detected.
     *
     * @param path                {@link Path} file which was updated
     * @param jambelConfiguration {@link JambelConfiguration} updated configuration
     */
    public void jambelUpdated(Path path, JambelConfiguration jambelConfiguration);
}
