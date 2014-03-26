package com.jambit.jambel.server.spring;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Classic dispatcher servlet for the Spring web application context.
 *
 * We use servlet 3.0 for async handling of REST requests to support long polling.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class DispatcherServletConfiguration implements WebApplicationInitializer {

    private static final String WEB_DISPATCHER_SERVLET_NAME = "webDispatcher";
    /**
     * Root servlet path for the dispatcher with the URL rewrite rule.
     * The sub path "web" is directly used in the controllers.
     */
    private static final String WEB_DISPATCHER_SERVLET_MAPPING = "/app/*";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationContextConfiguration.class);

        // common dispatcher for Spring MVC
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(WEB_DISPATCHER_SERVLET_NAME, new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.setAsyncSupported(true);
        dispatcher.addMapping(WEB_DISPATCHER_SERVLET_MAPPING);
        servletContext.addListener(new ContextLoaderListener(rootContext));
    }

}