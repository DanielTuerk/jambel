package com.jambit.jambel.hub;

import com.jambit.jambel.light.SignalLightStatus;

/**
 * Listener for changes of the {@link com.jambit.jambel.light.SignalLightStatus} for an {@link com.jambit.jambel.light.SignalLight},
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public interface LightStatusOnChangeListener {

    /**
     * Status of an {@link com.jambit.jambel.light.SignalLight} changed.
     *
     * @param newLightStatus {@link com.jambit.jambel.light.SignalLightStatus} new status
     */
    public void statusLightChanged(SignalLightStatus newLightStatus);
}
