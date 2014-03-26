package com.jambit.jambel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jambit.jambel.config.ConfigListener;
import com.jambit.jambel.config.ConfigManagement;
import com.jambit.jambel.config.ConfigPathWatchService;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.hub.init.JobInitializer;
import com.jambit.jambel.hub.init.LastStateStorageFactory;
import com.jambit.jambel.hub.lights.LightStatusCalculator;
import com.jambit.jambel.hub.retrieval.JobRetriever;
import com.jambit.jambel.hub.retrieval.JobStateRetriever;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.server.servlet.JenkinsNotificationsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Main module of the web application.
 * Loading the {@link SignalLight}s from config. Initialize and destroy the {@link SignalLight} at runtime by events of the
 * {@link ConfigListener}.
 * <p/>
 * This component mange all states of the {@link SignalLight} instances during the system runtime.
 */
@Component
public class Jambel implements ConfigListener {

    private static final Logger logger = LoggerFactory.getLogger(Jambel.class);

    private Map<Path, JambelInitializer> jambelInitializerInstances = Maps.newHashMap();

    @Autowired
    private ConfigManagement configManagement;

    @Autowired
    private SignalLightModule signalLightModule;

    @Autowired
    private JobRetriever jobRetriever;

    @Autowired
    private JobStateRetriever jobStateRetriever;

    @Autowired
    private LightStatusCalculator lightStatusCalculator;

    @Autowired
    private ScheduledExecutorService pollerExecutor;

    @Autowired
    private JenkinsNotificationsServlet jenkinsNotificationsServlet;

    @Autowired
    private JambelDestroyer destroyer;

    @Autowired
    private LastStateStorageFactory lastStateStorageFactory;

    /**
     * Single listener for the REST controller ({@link com.jambit.jambel.server.mvc.JambelController})
     * to track changes.
     */
    private ConfigListener jambelConfigListener;

    @PostConstruct
    public void init() {
        logger.info("loading jambels from config");
        for (Map.Entry<Path, JambelConfiguration> entry : configManagement.loadConfigFromFilePath().entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                jambelInitializerInstances.put(entry.getKey(), initJambel(entry.getValue()));
            } else {
                logger.warn("can't initialize jambel " + String.valueOf(entry.getKey()) + " configuration is null");
            }
        }
        ConfigPathWatchService.addListener(this);
        logger.info("system started");
    }

    @PreDestroy
    public void destroy() {
        logger.info("destroy jambels");
        ConfigPathWatchService.removeListener(this);
        // stop state polling from any jenkins
        pollerExecutor.shutdownNow();

        // TODO: shutdown not working, no red or sometimes no colors and no connections ...
        for (JambelInitializer jambelInitializer : jambelInitializerInstances.values()) {
            destroyJambel(jambelInitializer);
        }
        jambelInitializerInstances.clear();
        logger.info("system shutdown");
    }

    public void setJambelConfigListener(ConfigListener jambelConfigListener) {
        this.jambelConfigListener = jambelConfigListener;
    }

    /**
     * Get all active signal lights by the {@link JambelInitializer}.
     *
     * @return {@link List<JambelInitializer>}
     */
    public List<JambelInitializer> getJambelInitializerInstances() {
        return Lists.newArrayList(jambelInitializerInstances.values());
    }

    /**
     * Create the {@link JambelInitializer} for the given {@link JambelConfiguration} and execute the initializer.
     *
     * @param jambelConfiguration {@link JambelConfiguration} to initialize
     * @return {@link JambelInitializer} executed initializer
     */
    private JambelInitializer initJambel(JambelConfiguration jambelConfiguration) {
        SignalLight signalLight = signalLightModule.create(jambelConfiguration.getSignalLightConfiguration());

        JobStatusHub hub = new JobStatusHub(signalLight, lightStatusCalculator,
                lastStateStorageFactory.createStorage(jambelConfiguration.getSignalLightConfiguration()));
        JobInitializer jobInitializer = new JobInitializer(hub, jambelConfiguration, jobRetriever, jobStateRetriever,
                pollerExecutor, jenkinsNotificationsServlet);

        JambelInitializer jambelInitializer = new JambelInitializer(hub, jobInitializer, signalLight);
        jambelInitializer.init();
        return jambelInitializer;
    }

    /**
     * Destroy an active {@link JambelInitializer} by a {@link JambelDestroyer}.
     *
     * @param jambelInitializer {@link JambelInitializer} to destroy
     */
    private void destroyJambel(JambelInitializer jambelInitializer) {
        jenkinsNotificationsServlet.unsubscribe(jambelInitializer.getJobInitializer().getJambelConfiguration());
        destroyer.destroy(jambelInitializer.getSignalLight());
    }

    @Override
    public void jambelCreated(Path path, JambelConfiguration jambelConfiguration) {
        jambelInitializerInstances.put(path, initJambel(jambelConfiguration));
        if (jambelConfigListener != null) {
            jambelConfigListener.jambelCreated(path,jambelConfiguration);
        }
    }

    @Override
    public void jambelRemoved(Path path) {
        destroyJambel(jambelInitializerInstances.get(path));
        jambelInitializerInstances.remove(path);
        if (jambelConfigListener != null) {
            jambelConfigListener.jambelRemoved(path);
        }
    }

    @Override
    public void jambelUpdated(Path path, JambelConfiguration jambelConfiguration) {
        jambelInitializerInstances.put(path, initJambel(jambelConfiguration));
        if (jambelConfigListener != null) {
            jambelConfigListener.jambelUpdated(path,jambelConfiguration);
        }
    }
}