package com.jambit.jambel.server.mvc;

import com.jambit.jambel.hub.JobStatusHub;
import com.jambit.jambel.light.SignalLight;

public class MainController {

	private final JobStatusHub hub;
	private final SignalLight light;

	public MainController(JobStatusHub hub, SignalLight light) {
		this.hub = hub;
		this.light = light;
	}
//
//	@Path("/")
//	@View("main")
//	public ModelMap main(HttpServletRequest request) {
//		ModelMap model = new ModelMap();
//		model.put("jobs", hub.getLastStates());
//		model.put("light", hub.getSignalLight());
//		model.put("notificationUrl", request.getRequestURL() + ServerModule.JOBS_PATH);
//		model.put("lastStatus", hub.getStatus());
//
//		model.put("resetUrl", request.getRequestURL() + "/light/reset");
//		model.put("updateLightUrl", request.getRequestURL() + "/update");
//		model.put("lightWebUrl", "http://" + hub.getSignalLight().getConfiguration().getHostAndPort().getHostText()
//				+ ":80");
//
//
//		return model;
//	}
//
//	@Path("/update")
//	@View("postResult")
//	@Model("message")
//	public String update() {
//		hub.updateSignalLight();
//		return "success";
//	}
//
//	@Path("/light/reset")
//	@View("postResult")
//	@Model("message")
//	public String reset() {
//		try {
//			light.reset();
//			return "success";
//		}
//		catch (SignalLightNotAvailableException e) {
//			return e.getMessage();
//		}
//	}
}
