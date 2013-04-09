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
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
public class JenkinsNotificationsServlet implements JobStateReceiverRegistry {

    private Map<JambelConfiguration, JobStatusHub> jambelPostReceiver = Maps.newHashMap();

    @Override
    public void register(JambelConfiguration jambelConfiguration, JobStatusHub hub) {
        //To change body of implemented methods use File | Settings | File Templates.
        jambelPostReceiver.put(jambelConfiguration, hub);
    }

    @Override
    public void unRegister(JambelConfiguration jambelConfiguration) {
        jambelPostReceiver.remove(jambelConfiguration);
    }


//    private final JobStatusReceiver hub;
//
//    @Autowired
//    public JenkinsNotificationsServlet(JobStatusReceiver hub) {
//        this.hub = hub;
//    }

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

    @RequestMapping(value = "notifications/jenkins", method = RequestMethod.POST)
    public void doPost(HttpServletRequest req) throws ServletException, IOException {
        Gson gson = new Gson();
        final NotificationData data = gson.fromJson(new InputStreamReader(req.getInputStream()), NotificationData.class);
        MDC.put("phase", data.build.phase.toString());
        MDC.put("jobName", data.name);
        try {
//            hub.updateJobState(data.getJob(), data.getPhase(), data.getResult());
            //TODO: dispatch to jambel instances
            for (JambelConfiguration jambelConfiguration : jambelPostReceiver.keySet()) {
                for (JobConfiguration jobConfiguration : jambelConfiguration.getJobs()) {

                    String expectedHost = jobConfiguration.getJenkinsJobUrl().getHost();
                                            String jobConfigPath = jobConfiguration.getJenkinsJobUrl().getPath().replace("/","");

                    if ((req.getRemoteHost().equals(expectedHost) || req.getRemoteAddr().equals(expectedHost))
                            && jobConfigPath.equals(data.url.replace("/",""))) {
                        jambelPostReceiver.get(jambelConfiguration).updateJobState(data.getJob(), data.getPhase(), data.getResult());
                    }
                }
            }


        } finally {
            MDC.remove("jobName");
            MDC.remove("phase");
        }
    }


}
