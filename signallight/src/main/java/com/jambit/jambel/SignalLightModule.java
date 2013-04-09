package com.jambit.jambel;

import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.light.cmdctrl.CommandControlledSignalLight;
import com.jambit.jambel.light.cmdctrl.lan.LanCommandSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class SignalLightModule {
     private final ScheduledExecutorService signalLightStatusExecutor;

    @Autowired
    public SignalLightModule(ScheduledExecutorService signalLightStatusExecutor) {
        this.signalLightStatusExecutor = signalLightStatusExecutor;
    }


//    @PostConstruct
//	protected void configure() {
////		bind(SignalLight.class).to(CommandControlledSignalLight.class);
////		bind(SignalLightCommandSender.class).to(LanCommandSender.class).in(Singleton.class);
//
////		bind(ScheduledExecutorService.class).annotatedWith(Names.named("signalLight")).toInstance(
////				Executors.newSingleThreadScheduledExecutor(namedThreadFactory));
//
//        		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("status-updater-%d").build();
//       scheduledExecutorService= Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
//
//	}

//	public SignalLightConfiguration config(JambelConfiguration config) {
//		return config.getSignalLightConfiguration();
//	}

	/**
	 * Creates a new signal light with the JSON config found at the given path.
	 */
	public  SignalLight create(SignalLightConfiguration signalLightConfiguration) {
        return new CommandControlledSignalLight(signalLightConfiguration, new LanCommandSender(signalLightConfiguration),signalLightStatusExecutor);
//		return Guice.createInjector(new ConfigModule(configFilePath), new SignalLightModule()).getInstance(SignalLight.class);
	}

    @PreDestroy
    public void destroy() {
//        signalLightStatusExecutor.shutdownNow();
    }
}