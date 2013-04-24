package com.jambit.jambel;

import com.google.common.net.HostAndPort;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.config.jambel.SignalLightConfiguration.SlotPosition;
import com.jambit.jambel.light.LightMode;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.light.SignalLightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Destroyer for the jambel instances.
 * The component reset and destroy a active instance of a {@link SignalLight}.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Component
public class JambelDestroyer {

    private static final Logger logger = LoggerFactory.getLogger(JambelDestroyer.class);

    /**
     * Destroy and reset the lights of the given {@link SignalLight}.
     *
     * @param signalLight {@link SignalLight}
     */
    public void destroy(SignalLight signalLight) {
        resetSignalLight(signalLight);
        signalLight.shutdown();
    }

    /**
     * Reset the light with an shutdown lightning.
     *
     * @param signalLight {@link SignalLight}
     */
    private void resetSignalLight(SignalLight signalLight) {
        SignalLightConfiguration configuration = signalLight.getConfiguration();

        HostAndPort hostAndPort = configuration.getHostAndPort();
        if (signalLight.isAvailable()) {
            logger.info("resetting signal light at {}", hostAndPort);

            SignalLightStatus allOn = SignalLightStatus.all(LightMode.ON);
            sendAndWait(signalLight, allOn, 2000);
            // top to bottom sequence
            if (signalLight.getConfiguration().getGreen() == SlotPosition.top) {
                sendAndWait(signalLight, allOn.butGreen(LightMode.OFF), 500);
                sendAndWait(signalLight, allOn.butGreen(LightMode.OFF).butYellow(LightMode.OFF), 500);
            } else {
                sendAndWait(signalLight, allOn.butRed(LightMode.OFF), 500);
                sendAndWait(signalLight, allOn.butRed(LightMode.OFF).butYellow(LightMode.OFF), 500);
            }

            signalLight.reset();
        } else {
            logger.warn("cannot reset signal light at {}, it is not available", hostAndPort);
        }
    }

    /**
     * Send the light state to the signal light and wait for the given time period.
     *
     * @param signalLight  {@link SignalLight}
     * @param lightStatus  {@link SignalLightStatus}
     * @param milliseconds milliseconds to wait
     */
    private void sendAndWait(SignalLight signalLight, SignalLightStatus lightStatus, int milliseconds) {
        signalLight.setNewStatus(lightStatus);

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // do nothing and hope for a better world
        }
    }
}