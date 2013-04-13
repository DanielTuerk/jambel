package com.jambit.jambel.light.cmdctrl;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import com.jambit.jambel.light.LightMode;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.light.SignalLightNotAvailableException;
import com.jambit.jambel.light.SignalLightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sends ASCII commands using a {@link SignalLightCommandSender}.
 * 
 * @author "Florian Rampp (Florian.Rampp@jambit.com)"
 * 
 */
public final class CommandControlledSignalLight implements SignalLight {

	private static final Logger logger = LoggerFactory.getLogger(CommandControlledSignalLight.class.getName());

	private final SignalLightConfiguration configuration;

	private final SignalLightCommandSender commandSender;

	private final ScheduledExecutorService executor;

	public CommandControlledSignalLight(SignalLightConfiguration configuration, SignalLightCommandSender commandSender,
			ScheduledExecutorService executor) {
		this.configuration = configuration;
		this.commandSender = commandSender;
		this.executor = executor;
	}

	private Integer[] toIntValues(SignalLightStatus status) {
		Integer[] lightValues = { 0, 0, 0, 0 };
		lightValues[configuration.getNumberForGreen() - 1] = status.getGreen().getCode();
		lightValues[configuration.getNumberForYellow() - 1] = status.getYellow().getCode();
		lightValues[configuration.getNumberForRed() - 1] = status.getRed().getCode();
		return lightValues;
	}

	private SignalLightStatus toStatus(Integer[] values) {
		LightMode green = LightMode.forCode(values[configuration.getNumberForGreen() - 1]);
		LightMode yellow = LightMode.forCode(values[configuration.getNumberForYellow() - 1]);
		LightMode red = LightMode.forCode(values[configuration.getNumberForRed() - 1]);
		return SignalLightStatus.individual(green, yellow, red);
	}

	private void sendCommand(String command) {
		String response = commandSender.send(command);
		if (!response.equals("OK"))
			throw new RuntimeException("response to command '" + command + "' was '" + response + "', not 'OK'");
	}

	@Override
	public SignalLightStatus getCurrentStatus() {
		String response = commandSender.send("status");
		Pattern statusResponsePattern = Pattern.compile("^status=(\\d),(\\d),(\\d),(\\d),(\\d),(\\d)$");
		Matcher matcher = statusResponsePattern.matcher(response);
		if (matcher.matches()) {
			// the last two digits are ignored
			Integer[] values = new Integer[4];
			for (int i = 0; i < 4; i++) {
				values[i] = Integer.valueOf(matcher.group(i + 1));
			}
			return toStatus(values);
		}
		else {
			throw new RuntimeException("response " + response + " did not match pattern " + statusResponsePattern);
		}
	}

	private final class UpdateLightStatusTask implements Runnable {
		private boolean failOnNextExecution = false;

		private final SignalLightStatus newStatus;

		public UpdateLightStatusTask(SignalLightStatus newStatus) {
			this.newStatus = newStatus;
		}

		@Override
		public void run() {
			if (failOnNextExecution) {
				throw new RuntimeException();
			}

			final Integer[] lightValues = toIntValues(newStatus);

			try {
				sendCommand("set_all=" + Joiner.on(',').join(lightValues));
			}
			catch (SignalLightNotAvailableException e) {
				logger.warn("could not update signal light", e);
			}
		}

		public void failOnNextExecution() {
			failOnNextExecution = true;
		}
	}

	private UpdateLightStatusTask scheduledTask;

	@Override
	public void setNewStatus(SignalLightStatus newStatus) {
		Optional<Integer> keepAliveInterval = configuration.getKeepAliveInterval();
		if (keepAliveInterval.isPresent()) {
			if (scheduledTask != null) {
				// get rid of the old task (this is the only way to do it, that sucks!)
				scheduledTask.failOnNextExecution();
			}
			scheduledTask = new UpdateLightStatusTask(newStatus);
			executor.scheduleWithFixedDelay(scheduledTask, 0, keepAliveInterval.get(), TimeUnit.MILLISECONDS);
		}
		else {
			executor.schedule(new UpdateLightStatusTask(newStatus),1,TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void reset() {
		sendCommand("reset");
	}

	@Override
	public boolean isAvailable() {
		return commandSender.reachesSignalLight();
	}

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }

    @Override
	public SignalLightConfiguration getConfiguration() {
		return configuration;
	}

}
