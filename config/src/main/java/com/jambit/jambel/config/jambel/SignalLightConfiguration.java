package com.jambit.jambel.config.jambel;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;

public final class SignalLightConfiguration {

	public static enum SlotPosition {
		top, bottom
	}

	private String host;

	private int port;

	private int readTimeout;

	private SlotPosition green;

	private Integer keepAliveInterval;

	public SignalLightConfiguration(String host, int port, int readTimeout,
			SlotPosition green, Integer keepAliveInterval) {
		super();
		this.host = host;
		this.port = port;
		this.readTimeout = readTimeout;
		this.green = green;
		this.keepAliveInterval = keepAliveInterval;
	}

	public SignalLightConfiguration() {

	}

	public int getNumberForGreen() {
		return green == SlotPosition.top ? 3 : 1;
	}

	public int getNumberForYellow() {
		return 2;
	}

	public int getNumberForRed() {
		return green == SlotPosition.top ? 1 : 3;
	}

	public SlotPosition getGreen() {
		return green;
	}

	public HostAndPort getHostAndPort() {
		return HostAndPort.fromParts(host, port);
	}

	public int getReadTimeoutInMs() {
		return readTimeout;
	}

	public Optional<Integer> getKeepAliveInterval() {
		return Optional.fromNullable(keepAliveInterval);
	}

}