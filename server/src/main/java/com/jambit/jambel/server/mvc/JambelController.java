package com.jambit.jambel.server.mvc;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jambit.jambel.Jambel;
import com.jambit.jambel.JambelInitializer;
import com.jambit.jambel.config.jambel.JobConfiguration;
import com.jambit.jambel.hub.jobs.Job;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Main controller to show the jambels overview with the states and configurations.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class JambelController {
    private static final Logger LOG = LoggerFactory.getLogger(Jambel.class);

    @Autowired
    private Jambel jambel;

    /**
     * Show the view for the jambel overview.
     *
     * @return {@link ModelAndView}
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView showJambelView() {
        ModelAndView modelAndView = new ModelAndView("jambeln");
        modelAndView.addObject("initializers", jambel.getJambelInitializerInstances());
        return modelAndView;
    }

    @RequestMapping(value = "/signalLights", method = RequestMethod.GET)
    @ResponseBody
    public void getSignalLightStates(HttpServletResponse response) {
        Map<String, Object> jsonData = Maps.newHashMap();
        jsonData.put("signalLights", Lists.transform(jambel.getJambelInitializerInstances(), new Function<JambelInitializer, JambelWebModel>() {
            @Override
            public JambelWebModel apply(@Nullable com.jambit.jambel.JambelInitializer input) {
                JambelWebModel jambelWebModel = new JambelWebModel(input.getSignalLight().getConfiguration().getHostAndPort().getHostText(),
                        input.getSignalLight().getConfiguration().getHostAndPort().getPort(), input.getSignalLight().getCurrentStatus());

                for (JobConfiguration jobConfiguration : input.getJobInitializer().getJambelConfiguration().getJobs()) {
                    jambelWebModel.getJobsConfiguration().add(new JambelJobWebModel(jobConfiguration, input.getHub().getLastStates().get(
                            new Job(jobConfiguration.getJenkinsJobUrl().getPath().split("/")[2], jobConfiguration.getJenkinsJobUrl().toString()))));
                }

                return jambelWebModel;
            }
        }));
        try {
            writeJsonToResponse(new Gson().toJson(jsonData), response);
        } catch (IOException e) {
            LOG.error("can't create JSON signal lights", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
    public static void writeJsonToResponse(String json, HttpServletResponse response) throws IOException {
        AbstractHttpMessageConverter<String> stringHttpMessageConverter = new StringHttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;
        if (stringHttpMessageConverter.canWrite(String.class, jsonMimeType)) {
            ServletServerHttpResponse res = new ServletServerHttpResponse(response);
            stringHttpMessageConverter.write(json, jsonMimeType, res);
        } else {
            throw new IOException("can't write String for mime type: " + jsonMimeType);
        }
    }

}
