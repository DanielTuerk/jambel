package com.jambit.jambel.server.mvc;

import com.jambit.jambel.Jambel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Controller
public class JambelController {

    @Autowired
    private Jambel jambel;

    @RequestMapping(value = "jambeln", method = RequestMethod.GET)
    public ModelAndView showJambelView() {
        ModelAndView modelAndView = new ModelAndView("jambeln");
        modelAndView.addObject("initializers",jambel.getJambelInitializers());
        return modelAndView;
    }
}
