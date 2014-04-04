package com.jambit.jambel.server.mvc;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jambit.jambel.Jambel;
import com.jambit.jambel.JambelInitializer;
import com.jambit.jambel.config.ConfigListener;
import com.jambit.jambel.config.ConfigManagement;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.config.jambel.JobConfiguration;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.config.jambel.SignalLightConfiguration.SlotPosition;
import com.jambit.jambel.hub.LightStatusOnChangeListener;
import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.light.SignalLightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Main controller to show the jambels overview with the states and
 * configurations.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class JambelController implements ConfigListener, LightStatusOnChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(Jambel.class);
    private static final String FLAG_INITIAL_LIST_RECEIVED = "initialListReceived";

    private final Jambel jambel;

    private final ConfigManagement configManagement;

    private final Queue<LinkedBlockingQueue<Boolean>> listeners = new ConcurrentLinkedQueue<>();

    @Autowired
    public JambelController(Jambel jambel, ConfigManagement configManagement) {
        this.jambel = jambel;
        this.configManagement = configManagement;

        jambel.setJambelConfigListener(this);
        initLightStatusCallback();
    }


    /**
     * Show the view for the jambel overview.
     *
     * @return {@link ModelAndView}
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView showJambelView() {
        ModelAndView modelAndView = new ModelAndView("jambeln");
        modelAndView.addObject("initializers",
                jambel.getJambelInitializerInstances());
        return modelAndView;
    }

    @RequestMapping(value = {"/signalLights"}, method = RequestMethod.GET)
    @ResponseBody
    public void getSignalLightStates(HttpSession session, HttpServletResponse response) {

        // TODO: fix me: changes during new request won't be tracked

        LinkedBlockingQueue<Boolean> queue = new LinkedBlockingQueue<>();
        listeners.add(queue);

        // long polling
        try {
            if (queue.poll(1, TimeUnit.MINUTES) != null) {
                getAllSignalLightStates(session, response);
            } else {
//                listeners.remove(queue);
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
            listeners.remove(queue);
        } catch (InterruptedException e) {
            LOG.error("queue interrupted", e);
        }

    }

    /** updates one jambel with index {index} according to json string param */
    @RequestMapping(value = {"/signalLights/{index}"}, method = RequestMethod.POST)
    @ResponseBody
    public void updateJobConfiguration(@RequestBody String jobConfigurationsIn, @PathVariable int index,
            HttpServletResponse response) {
        LOG.info("POST request" + jobConfigurationsIn.toString());

        // json string to new job config object (
        WebModelJobConfigurationsData one = new Gson().fromJson(jobConfigurationsIn, WebModelJobConfigurationsData.class);

        LOG.info("data: " + one);

        // load jambel by index
        JambelInitializer jambelInitializer = jambel.getJambelInitializerInstances().get(index);
        JambelConfiguration jambelConfiguration = jambelInitializer.getJobInitializer().getJambelConfiguration();

        // replace job config in jambel with object 'one'
        Collection<JobConfiguration> jobConfigurations = jambelConfiguration.getJobs();
        // btw: this is a dodgy way to set a member!
        jobConfigurations.clear();
        jobConfigurations.addAll(Arrays.asList(one.jobs));

        // save modified jambel , create    configManagement.modify()...
        try {
            configManagement.createConfigFile(jambelConfiguration);
        } catch (Exception e) {
            LOG.error("can't update jobs", e);
        }
    }

    @RequestMapping(value = {"/signalLights"}, method = RequestMethod.PUT)
    public ResponseEntity<String> createJambel(@RequestParam String ip, @RequestParam int port, @RequestParam String colors) {
        SignalLightConfiguration signalLightConfiguration = new SignalLightConfiguration(ip, port, 500, "RYG".equals(colors) ? SlotPosition.bottom : SlotPosition.top, 3000);
        HttpHeaders headers = new HttpHeaders();
        try {
            configManagement.createConfigFile(new JambelConfiguration(signalLightConfiguration));
            return new ResponseEntity<>("", headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("can't create jambel", e);
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = {"/signalLights/all"}, method = RequestMethod.GET)
    @ResponseBody
    public void getAllSignalLightStates(HttpSession session,
            HttpServletResponse response) {
        try {
            Map<String, Object> jsonData = Maps.newHashMap();
            jsonData.put("signalLights", Lists.transform(
                    jambel.getJambelInitializerInstances(),
                    new Function<JambelInitializer, JambelWebModel>() {
                        @Override
                        public JambelWebModel apply(
                                com.jambit.jambel.JambelInitializer input) {
                            JambelWebModel jambelWebModel = new JambelWebModel(
                                    input.getSignalLight().getConfiguration()
                                            .getHostAndPort().getHostText(), input
                                    .getSignalLight().getConfiguration()
                                    .getHostAndPort().getPort(), input
                                    .getHub().getStatus()
                            );

                            for (JobConfiguration jobConfiguration : input
                                    .getJobInitializer().getJambelConfiguration()
                                    .getJobs()) {
                                jambelWebModel
                                        .getJobsConfiguration()
                                        .add(new JambelJobWebModel(
                                                jobConfiguration,
                                                input.getHub()
                                                        .getLastStates()
                                                        .get(new Job(
                                                                jobConfiguration
                                                                        .getJenkinsJobUrl()
                                                                        .getPath()
                                                                        .split("/")[2],
                                                                jobConfiguration
                                                                        .getJenkinsJobUrl()
                                                                        .toString()
                                                        ))
                                        ));
                            }

                            return jambelWebModel;
                        }
                    }
            ));
            writeJsonToResponse(new Gson().toJson(jsonData), response);

            session.setAttribute(FLAG_INITIAL_LIST_RECEIVED, true);
        } catch (Exception e) {
            LOG.error("can't create JSON signal lights", e);
            try {
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getMessage());
            } catch (IOException e1) {
                LOG.error("can't send error response", e1);
            }
        }
    }

    /**
     * Write the given json to the output of the servlet response.
     *
     * @param json     data to write
     * @param response response to write into
     * @throws java.io.IOException can't write to response
     */
    public static void writeJsonToResponse(String json,
            HttpServletResponse response) throws IOException {
        AbstractHttpMessageConverter<String> stringHttpMessageConverter = new StringHttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;
        if (stringHttpMessageConverter.canWrite(String.class, jsonMimeType)) {
            ServletServerHttpResponse res = new ServletServerHttpResponse(
                    response);
            stringHttpMessageConverter.write(json, jsonMimeType, res);
        } else {
            throw new IOException("can't write String for mime type: "
                    + jsonMimeType);
        }
    }

    @Override
    public void jambelCreated(Path path, JambelConfiguration jambelConfiguration) {
        initLightStatusCallback();
        offerQueues();
    }

    @Override
    public void jambelRemoved(Path path) {
        initLightStatusCallback();
        offerQueues();
    }

    @Override
    public void jambelUpdated(Path path, JambelConfiguration jambelConfiguration) {
        offerQueues();
    }

    private void offerQueues() {
        for (Queue<Boolean> queue : listeners) {
            queue.offer(true);
        }
    }

    @Override
    public void statusLightChanged(SignalLightStatus newLightStatus) {
        offerQueues();
    }

    private void initLightStatusCallback() {
        for (JambelInitializer jambelInitializer : jambel.getJambelInitializerInstances()) {
            jambelInitializer.getHub().removeLightStatusListener(this);
            jambelInitializer.getHub().addLightStatusListener(this);
        }
    }

    @PreDestroy
    public void cleanUp() {
        for(Queue queue:listeners){
            queue.clear();
        }
        listeners.clear();
    }
}
