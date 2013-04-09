package com.jambit.jambel.light;

import com.jambit.jambel.config.jambel.SignalLightConfiguration;

public interface SignalLight {

	public SignalLightConfiguration getConfiguration();

	void setNewStatus(SignalLightStatus newStatus);

	// TODO: blink times, ...

	/**
	 * @throws SignalLightNotAvailableException if no connection can be established to the signal
	 *             light
	 */
	SignalLightStatus getCurrentStatus();

	/**
	 * @throws SignalLightNotAvailableException if no connection can be established to the signal
	 *             light
	 */
	void reset();

	boolean isAvailable();

}
