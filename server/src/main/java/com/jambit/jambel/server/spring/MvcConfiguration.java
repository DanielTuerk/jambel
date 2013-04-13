package com.jambit.jambel.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;

/**
 * Spring MVC configuration:
 * <p/>
 * <ul>
 * <li>view resolver for JSPs</li>
 * <li>mapping root request to welcome page</li>
 * </ul>
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Configuration
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter {

    public static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
    public static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    public static final String VIEW_RESOLVER_COMMON_PREFIX = VIEW_RESOLVER_PREFIX + "layout/";

    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
        beanNameViewResolver.setOrder(1);
        return beanNameViewResolver;
    }

    @Bean
    public ViewResolver viewResolver() {
        UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setOrder(2);
        viewResolver.setViewClass(TilesView.class);
        return viewResolver;
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // TODO: map to controller request mappging
//        registry.addViewController("/").setViewName("jambeln");
    }

    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setDefinitionsFactoryClass(JspDefinitionsFactory.class);
        tilesConfigurer.setDefinitions(new String[]{});
        return tilesConfigurer;
    }

}
