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

    @Bean
    public String jambelHomePath() {
        File configPath = new File(System.getProperty("user.home") + "/.jambel/");
        if (!configPath.exists()) {
            configPath.mkdirs();
        }
        return configPath.getAbsolutePath();
    }

    @Bean
    public String configFilePath() {
        return jambelHomePath() + "/config/";
    }

    @Bean
    public String storageFilePath() {
        return jambelHomePath() + "/storage/";
    }

}
