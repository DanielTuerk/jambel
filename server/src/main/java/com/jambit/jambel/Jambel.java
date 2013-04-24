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

    @PostConstruct
    public void init() {
        for (Map.Entry<Path, JambelConfiguration> entry : configManagement.loadConfigFromFilePath().entrySet()) {
            jambelInitializerInstances.put(entry.getKey(), initJambel(entry.getValue()));
        }
        ConfigPathWatchService.addListener(this);
    }

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

    @PreDestroy
    public void destroy() {
        ConfigPathWatchService.removeListener(this);

        pollerExecutor.shutdownNow();

        // TODO: shutdown not working, no red or sometimes no colors and no connections ...
        for (JambelInitializer jambelInitializer : jambelInitializerInstances.values()) {
            destroyJambel(jambelInitializer);
        }
        jambelInitializerInstances.clear();
    }

    public List<JambelInitializer> getJambelInitializerInstances() {
        return Lists.newArrayList(jambelInitializerInstances.values());
    }

    private void destroyJambel(JambelInitializer jambelInitializer) {
        jenkinsNotificationsServlet.unsubscribe(jambelInitializer.getJobInitializer().getJambelConfiguration());
        destroyer.destroy(jambelInitializer.getSignalLight());
    }

    @Override
    public void jambelCreated(Path path, JambelConfiguration jambelConfiguration) {
        jambelInitializerInstances.put(path, initJambel(jambelConfiguration));
    }

    @Override
    public void jambelRemoved(Path path) {
        destroyJambel(jambelInitializerInstances.get(path));
        jambelInitializerInstances.remove(path);
    }

    @Override
    public void jambelUpdated(Path path, JambelConfiguration jambelConfiguration) {
        jambelRemoved(path);
        jambelCreated(path, jambelConfiguration);
    }
}