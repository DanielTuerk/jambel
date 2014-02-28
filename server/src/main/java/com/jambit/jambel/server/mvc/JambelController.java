package com.jambit.jambel.server.mvc;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jambit.jambel.Jambel;
import com.jambit.jambel.JambelInitializer;
import com.jambit.jambel.config.ConfigManagement;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import com.jambit.jambel.config.jambel.JobConfiguration;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.config.jambel.SignalLightConfiguration.SlotPosition;
import com.jambit.jambel.hub.jobs.Job;



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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Main controller to show the jambels overview with the states and
 * configurations.
 * 
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class JambelController {
	private static final Logger LOG = LoggerFactory.getLogger(Jambel.class);
	private static final String FLAG_INITIAL_LIST_RECEIVED = "initialListReceived";


	@Autowired
	private Jambel jambel;
	
	 @Autowired
	    private ConfigManagement configManagement;

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

	@RequestMapping(value = { "/signalLights" }, method = RequestMethod.GET)
	@ResponseBody
	public void getSignalLightStates(HttpSession session,
			HttpServletResponse response) {

			try {
				Thread.sleep(60000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		getAllSignalLightStates(session, response);
	}

	
	@RequestMapping(value = { "/signalLights/{index}" }, method = RequestMethod.POST)
	@ResponseBody
	public void updateJobConfiguration(@RequestBody String jobConfigurations, @PathVariable int index, 
			HttpServletResponse response) {
		LOG.info("POST request" + jobConfigurations.toString());
		
		WebModelJobConfigurationsData one = new Gson().fromJson(jobConfigurations, WebModelJobConfigurationsData.class);
		LOG.info("data: "+one);
		
	}
	
	@RequestMapping(value = { "/signalLights" }, method = RequestMethod.PUT)
	public ResponseEntity<String> createJambel(@RequestParam String ip, @RequestParam int port, @RequestParam String colors) {
		SignalLightConfiguration signalLightConfiguration = new SignalLightConfiguration(ip,port,500, "RYG".equals(colors) ? SlotPosition.bottom : SlotPosition.top, 3000);
		 HttpHeaders headers = new HttpHeaders();
		try {
			configManagement.createConfigFile(new JambelConfiguration(signalLightConfiguration));
			 return new ResponseEntity<>("", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("can't create jambel",e);
			 return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = { "/signalLights/{all}" }, method = RequestMethod.GET)
	@ResponseBody
	public void getAllSignalLightStates(HttpSession session,
			HttpServletResponse response) {

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
										.getHub().getStatus());

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
																	.toString()))));
						}

						return jambelWebModel;
					}
				}));
		try {
			writeJsonToResponse(new Gson().toJson(jsonData), response);

			session.setAttribute(FLAG_INITIAL_LIST_RECEIVED, true);
		} catch (IOException e) {
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
	 * @param json
	 *            data to write
	 * @param response
	 *            response to write into
	 * @throws java.io.IOException
	 *             can't write to response
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

}
