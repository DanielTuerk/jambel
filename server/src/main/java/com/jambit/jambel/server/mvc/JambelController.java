package com.jambit.jambel.server.mvc;

import com.google.common.base.Strings;
import com.jambit.jambel.Jambel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Main controller to show the jambels overview with the states and configurations.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class JambelController {

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

    @RequestMapping(value = "/signalLightState", method = RequestMethod.GET)
    public void getSignalLightStates(@RequestParam String ipAndPort) {
        if(!Strings.isNullOrEmpty(ipAndPort)) {

        }

    }

}
