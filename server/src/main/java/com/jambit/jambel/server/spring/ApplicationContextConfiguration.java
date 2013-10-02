package com.jambit.jambel.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;

/**
 * Spring application context.
 * <p/>
 * <ul>
 * <li>perform the component scan</li>
 * <li>detect the user home and create workspace</li>
 * <li>set the 'config' path in the workspace</li>
 * <li>set the 'storage' path in the workspace</li>
 * </ul>
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Configuration
@ComponentScan(basePackages = {"com.jambit.jambel"})
@EnableAsync
@EnableAspectJAutoProxy
public class ApplicationContextConfiguration {

    public static final String JAMBEL_HOME = "/.jambel/";
    public static final String JAMBEL_HOME_CONFIG = "/config/";
    public static final String JAMBEL_HOME_STORAGE = "/storage/";

    @Bean
    public String jambelHomePath() {
        File configPath = new File(System.getProperty("user.home") + JAMBEL_HOME);
        if (!configPath.exists()) {
            if(!configPath.mkdirs()) {
                throw new RuntimeException("can't create the JAMBEL HOME path: " + configPath.getAbsolutePath());
            }
        }
        return configPath.getAbsolutePath();
    }

    @Bean
    public String configFilePath() {
        return jambelHomePath() + JAMBEL_HOME_CONFIG;
    }

    @Bean
    public String storageFilePath() {
        return jambelHomePath() + JAMBEL_HOME_STORAGE;
    }

}
