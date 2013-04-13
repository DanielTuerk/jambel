package com.jambit.jambel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.light.cmdctrl.CommandControlledSignalLight;
import com.jambit.jambel.light.cmdctrl.lan.LanCommandSender;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Component
public class SignalLightModule {

    /**
     * Creates a new signal light with the JSON config found at the given path.
     */
    public SignalLight create(SignalLightConfiguration signalLightConfiguration) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("light-status-updater-%d").build();
        ScheduledExecutorService signalLightStatusExecutor = Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
        return new CommandControlledSignalLight(signalLightConfiguration, new LanCommandSender(signalLightConfiguration), signalLightStatusExecutor);
    }

    @PreDestroy
    public void destroy() {
    }
}