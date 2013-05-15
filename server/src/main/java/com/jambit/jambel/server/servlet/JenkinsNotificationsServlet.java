package com.jambit.jambel.server.servlet;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.config.jambel.JobConfiguration;
import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.hub.jobs.JobState;
import com.jambit.jambel.hub.poster.JobStateReceiverRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Entry point for the Jenkins Poster plugin.
 * Each Jenkins-Job sends his states to this servlet. The servlet match the job states to the configured (and registered) jambel instances.
 *
 * @author Florian Ramp
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class JenkinsNotificationsServlet implements JobStateReceiverRegistry {

    private static final Logger logger = LoggerFactory.getLogger(JenkinsNotificationsServlet.class);

    /**
     * Registered hubs to map the notification data.
     */
    private Map<JambelConfiguration, JobStatusHub> jambelPostReceiver = Maps.newHashMap();

    @Override
    public void subscribe(JambelConfiguration jambelConfiguration, JobStatusHub hub) {
        jambelPostReceiver.put(jambelConfiguration, hub);
    }

    @Override
    public void unsubscribe(JambelConfiguration jambelConfiguration) {
        jambelPostReceiver.remove(jambelConfiguration);
    }

    /**
     * Model for the received JSON data of the notification plugin.
     * Only used in this servlet, the data is mapped to the models of the hub module.
     */
    private static class NotificationData {
        public String name;
        public String url;
        public BuildData build;

        private static class BuildData {
            public String full_url;
            public int number;
            public JobState.Phase phase;
            public JobState.Result status;
            public String url;
        }

        public Job getJob() {
            return new Job(name, url);
        }

        public JobState.Phase getPhase() {
            return build.phase;
        }

        public Optional<JobState.Result> getResult() {
            return Optional.fromNullable(build.status);
        }
    }

    /**
     * Servlet mapping for the Jenkins notification plugin.
     *
     * @param req {@link HttpServletRequest}
     * @param response never used by required to get the mapping in the dispatcher for the notification plugin
     */
    @RequestMapping(value = "/notifications/jenkins")
    public void doPost(HttpServletRequest req, HttpServletResponse response) {
        Gson gson = new Gson();
        try {
            final NotificationData data = gson.fromJson(new InputStreamReader(req.getInputStream()), NotificationData.class);
            MDC.put("phase", data.build.phase.toString());
            MDC.put("jobName", data.name);
            for (JambelConfiguration jambelConfiguration : jambelPostReceiver.keySet()) {
                for (JobConfiguration jobConfiguration : jambelConfiguration.getJobs()) {

                    String expectedHost = jobConfiguration.getJenkinsJobUrl().getHost();
                    String jobConfigPath = jobConfiguration.getJenkinsJobUrl().getPath().replace("/", "");

                    logger.debug(expectedHost + "," + jobConfigPath + "," + req.getRemoteHost() + "," + req.getRemoteAddr() + "," + jobConfigPath);

                    if ((req.getRemoteHost().equals(expectedHost) || req.getRemoteAddr().equals(expectedHost))
                            && jobConfigPath.equals(data.url.replace("/", ""))) {
                        data.url = jobConfiguration.getJenkinsJobUrl().toString();
                        jambelPostReceiver.get(jambelConfiguration).updateJobState(data.getJob(), data.getPhase(), data.getResult());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("can't read request body", e);
        } finally {
            MDC.remove("jobName");
            MDC.remove("phase");
        }
    }
}
