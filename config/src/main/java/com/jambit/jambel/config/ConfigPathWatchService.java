package com.jambit.jambel.config;

import com.google.common.collect.Lists;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Watch service for the configuration files.
 * The runnable listen to the <code>configFilePath</code> directory for modifications.
 *
 * Only JSON files are expected in the directory and each watch event will be mapped to the {@link ConfigListener}.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public final class ConfigPathWatchService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ConfigPathWatchService.class);

    /**
     * Listener instances for the watch event keys.
     */
    private static final List<ConfigListener> LISTENERS = Lists.newArrayList();

    /**
     * Add listener for modification events.
     *
     * @param listener {@link ConfigListener}
     */
    public static void addListener(ConfigListener listener) {
        LISTENERS.add(listener);
    }

    /**
     * Remove the given and already registered listener.
     * @param listener             {@link ConfigListener}
     */
    public static void removeListener(ConfigListener listener) {
        LISTENERS.remove(listener);
    }

    /**
     * Path to the directory of the configuration files.
     */
    private final String configFilePath;

    /**
     * Create new watch service to listen at the given file path.
     *
     * @param configFilePath {@link String} path to the directory
     */
    public ConfigPathWatchService(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @Override
    public void run() {
        Path path = Paths.get(configFilePath);
        try {
            // register watch service
        WatchService watchService = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            try {
                // wait for modifications
                watchService.take();
            } catch (InterruptedException e) {
                logger.error("config file watch service interrupted", e);
                return;
            }

            for (WatchEvent<?> event : watchKey.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // This key is registered only for ENTRY_CREATE events,
                // but an OVERFLOW event can occur regardless if events are lost or discarded.
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // The filename is the context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();

                // send events for the modification at the jambel configurations
                for (ConfigListener listener : LISTENERS) {
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        listener.jambelRemoved(filename);
                    } else {
                        Path child = path.resolve(filename);
                        JambelConfiguration jambelConfiguration = ConfigUtils.loadConfigFromPath(child);

                        if (jambelConfiguration != null) {
                            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                listener.jambelCreated(filename,jambelConfiguration);
                            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                listener.jambelUpdated(filename,jambelConfiguration);
                            } else {
                                logger.error("unknown Watch Event " + kind);
                            }
                        } else {
                            logger.error("couldn't load jambel configuration for " + filename + " by event " + kind);
                        }
                    }
                }
            }

            // Reset the key -- this step is critical if you want to receive further watch events.
            // If the key is no longer valid, the directory is inaccessible so exit the loop.
            boolean valid = watchKey.reset();
            if (!valid) {
                throw new RuntimeException("watch key no longer valid");
            }
        } catch (IOException e) {
            throw new RuntimeException("can't register watch service for the config path: " + configFilePath, e);
        }
    }
}
