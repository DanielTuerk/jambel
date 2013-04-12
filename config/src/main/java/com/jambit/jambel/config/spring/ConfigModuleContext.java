package com.jambit.jambel.config.spring;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Spring module context for the 'config' module.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Configuration
public class ConfigModuleContext {

    /**
     * Create scheduled executor to run the {@link com.jambit.jambel.config.ConfigPathWatchService}.
     *
     * @return {@link ScheduledExecutorService}
     */
    @Bean
    public ScheduledExecutorService configFilesWatchServiceExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("config-watch-service-%d").build();
        return Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
    }

}
