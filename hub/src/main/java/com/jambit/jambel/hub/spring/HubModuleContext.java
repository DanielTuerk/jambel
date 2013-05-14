package com.jambit.jambel.hub.spring;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Spring module context for the 'hub' module.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Configuration
public class HubModuleContext {

	private final static int POLLING_THREADS = 5;

    @Bean
    public ScheduledExecutorService pollerExecutor() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("poller-%d").build();
        return Executors.newScheduledThreadPool(HubModuleContext.POLLING_THREADS, namedThreadFactory);
    }

}
