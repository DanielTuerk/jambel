package com.jambit.jambel.server.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class LoggingController {

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public ModelAndView showLog() {
        ModelAndView modelAndView = new ModelAndView("log");
        modelAndView.addObject("messages", LoggingAppender.getMessages());
        return modelAndView;
    }
}
