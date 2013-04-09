package com.jambit.jambel;

import com.google.common.collect.Lists;
import com.jambit.jambel.config.ConfigManagement;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.hub.init.JobInitializer;
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
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class Jambel {

    private static final Logger logger = LoggerFactory.getLogger(Jambel.class);

    private List<JambelInitializer> jambelInitializers = Lists.newArrayList();

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

    @PostConstruct
    public void init() {

        for (JambelConfiguration jambelConfiguration : configManagement.getJambelConfigurations().values()) {
            initJambel(jambelConfiguration);
        }

    }

    private void initJambel(JambelConfiguration jambelConfiguration) {
        SignalLight signalLight = signalLightModule.create(jambelConfiguration.getSignalLightConfiguration());

        JobStatusHub hub = new JobStatusHub(signalLight, lightStatusCalculator);
        JobInitializer jobInitializer = new JobInitializer(hub, jambelConfiguration, jobRetriever, jobStateRetriever, pollerExecutor, jenkinsNotificationsServlet);

        JambelInitializer jambelInitializer = new JambelInitializer(hub, jobInitializer, signalLight);
        jambelInitializer.init();

        jambelInitializers.add(jambelInitializer);
    }

    private void destroyJambel(JambelInitializer jambelInitializer) {
        jenkinsNotificationsServlet.unRegister(jambelInitializer.getJobInitializer().getJambelConfiguration());
        destroyer.destroy(jambelInitializer.getSignalLight());
    }

    @Autowired
    private ScheduledExecutorService signalLightStatusExecutor;


    @PreDestroy
    public void destroy() {
        pollerExecutor.shutdownNow();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {


        }

        for (JambelInitializer jambelInitializer : jambelInitializers) {
            destroyJambel(jambelInitializer);
        }
        signalLightStatusExecutor.shutdownNow();

    }

    public List<JambelInitializer> getJambelInitializers() {
        return jambelInitializers;
    }
}