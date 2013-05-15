package com.jambit.jambel.server.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Show the help view.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class HelpController {

    /**
     * Loading the model and view.
     *
     * @return {@link org.springframework.web.servlet.ModelAndView}
     */
    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public ModelAndView showLog() {
        return new ModelAndView("help");
    }
}
