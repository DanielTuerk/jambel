package com.jambit.jambel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.light.cmdctrl.CommandControlledSignalLight;
import com.jambit.jambel.light.cmdctrl.lan.LanCommandSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Common module for the signal light components.
 */
@Component
public class SignalLightModule {

    /**
     * Creates a new signal light for the {@link SignalLightConfiguration}.
     * Each {@link SignalLight} get an own {@link ScheduledExecutorService} to send state updates to the light
     * and to stop the threads in the {@see com.jambit.jambel.light.SignalLight#destroy()}.
     *
     * @param signalLightConfiguration {@link SignalLightConfiguration}
     */
    public SignalLight create(SignalLightConfiguration signalLightConfiguration) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("light-status-updater-%d").build();
        ScheduledExecutorService signalLightStatusExecutor = Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
        return new CommandControlledSignalLight(signalLightConfiguration, new LanCommandSender(signalLightConfiguration), signalLightStatusExecutor);
    }

}