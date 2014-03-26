package com.jambit.jambel.hub;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.jambit.jambel.hub.init.LastStateStorage;
import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.hub.jobs.JobState;
import com.jambit.jambel.hub.lights.LightStatusCalculator;
import com.jambit.jambel.light.SignalLight;
import com.jambit.jambel.light.SignalLightNotAvailableException;
import com.jambit.jambel.light.SignalLightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Hub to receive the job states and calculate the signal light color by the job state.
 *
 * @author frampp
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public final class JobStatusHub implements JobStatusReceiver {

    private static final Logger logger = LoggerFactory.getLogger(JobStatusHub.class);

    private final SignalLight light;
    private final LightStatusCalculator calculator;

    private final Map<Job, JobState> lastStates;

    private final LastStateStorage lastStateStorage;

    private final Queue<LightStatusOnChangeListener> listeners = new ConcurrentLinkedQueue<>();

    public JobStatusHub(SignalLight light, LightStatusCalculator calculator, LastStateStorage lastStateStorage) {
        this.light = light;
        this.calculator = calculator;
        this.lastStateStorage = lastStateStorage;
        this.lastStates = Maps.newLinkedHashMap();

    }

    public void addJob(Job job, JobState lastState) {
        lastStates.put(job, lastState);
    }

    public Map<Job, JobState> getLastStates() {
        return Collections.unmodifiableMap(lastStates);
    }

    /**
     * @throws SignalLightNotAvailableException
     *
     */
    public void updateSignalLight() {
        updateLightStatus();
    }

    private void updateLightStatus() {
        SignalLightStatus newLightStatus = calculator.calc(lastStates.values());
        light.setNewStatus(newLightStatus);
        logger.debug("updated signal light with new status '{}'", newLightStatus);
        for(LightStatusOnChangeListener listener: listeners) {
            listener.statusLightChanged(newLightStatus);
        }
    }

    public void addLightStatusListener(LightStatusOnChangeListener lightStatusOnChangeListener) {
        listeners.add(lightStatusOnChangeListener);
    }

    public void removeLightStatusListener(LightStatusOnChangeListener lightStatusOnChangeListener) {
        listeners.remove(lightStatusOnChangeListener);
    }


    @Override
    public void updateJobState(Job job, JobState.Phase phase, Optional<JobState.Result> result) {
        if (!lastStates.containsKey(job)) {
            logger.warn("Received a job update for job '{}' but job was not registered. Add job to Jambel "
                    + "configuration first.", job);
            return;
        }

        JobState newState = calcNewState(job, phase, result);
        JobState oldState = lastStates.get(job);
        // short cut (no need to log or update signal light if old == new)
        if (oldState.equals(newState)) {
            return;
        }
        lastStates.put(job, newState);

        // store the states for initializing at startup
        // TODO: maybe other location for the call, because each update of the state will be stored (e.g. polling)
        lastStateStorage.storeJobStates(lastStates);

        // LOG
        switch (phase) {
            case STARTED:
                logger.info("job '{}' started to build", job);
                break;
            case FINISHED:
            case COMPLETED:
                logger.info("job '{}' {} to build", job, phase.toString().toLowerCase());
        }

        // UPDATE SIGNAL LIGHT
        updateLightStatus();
    }

    private JobState calcNewState(Job job, JobState.Phase newPhase, Optional<JobState.Result> newResult) {
        switch (newPhase) {
            case STARTED:
                // we have no state when phase is starting => use the last result
                return new JobState(newPhase, lastStates.get(job).getLastResult());
            case FINISHED:
            case COMPLETED:
                return new JobState(newPhase, newResult.get());
            default:
                throw new UnsupportedOperationException("phase " + newPhase + " not known");
        }
    }

    public SignalLight getSignalLight() {
        return light;
    }

    public SignalLightStatus getStatus() {
        return calculator.calc(lastStates.values());
    }

    public LastStateStorage getLastStateStorage() {
        return lastStateStorage;
    }
}
