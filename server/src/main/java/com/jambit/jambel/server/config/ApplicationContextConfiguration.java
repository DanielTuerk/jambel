package com.jambit.jambel.server.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jambit.jambel.hub.HubModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Configuration
@ComponentScan(basePackages = {"com.jambit.jambel"})
@EnableAsync
@EnableAspectJAutoProxy
public class ApplicationContextConfiguration {

    @Bean
    public String configFilePath() {
        File configPath = new File(System.getProperty("user.home")  +"/.jambel/");
        if(!configPath.exists()){
            configPath.mkdirs();
        }
        return configPath.getAbsolutePath();

    }

    @Bean
    public ScheduledExecutorService pollerExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("poller-%d").build();
		return Executors.newScheduledThreadPool(HubModule.POLLING_THREADS, namedThreadFactory);
    }

    @Bean
    public ScheduledExecutorService signalLightStatusExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("light-status-updater-%d").build();
//        return Executors.newSingleThreadExecutor(namedThreadFactory);//newScheduledThreadPool(HubModule.POLLING_THREADS, namedThreadFactory);
        return Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
    }

}
