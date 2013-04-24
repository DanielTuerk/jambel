package com.jambit.jambel.hub.init;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jambit.jambel.hub.jobs.Job;
import com.jambit.jambel.hub.jobs.JobState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Storage for an jambel to persist the last build states.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class LastStateStorage {

    private static final Logger logger = LoggerFactory.getLogger(LastStateStorage.class.getName());

    private Map<Job, JobState> lastStatesFromFile= Maps.newLinkedHashMap();

    /**
     * Path to the JSON file.
     */
    private final Path storageJsonFile;

    /**
     * Create a new storage from the given file.
     *
     * @param storageJsonFile {@link Path}
     */
    public LastStateStorage(Path storageJsonFile) {
        this.storageJsonFile = storageJsonFile;
        readFile();
    }

    /**
     * Load the latest stored state of the given {@link Job}.
     *
     * TODO: not working
     *
     * @param job {@link Job} to load state for
     * @return latest {@link JobState} or default
     */
    public JobState loadLastState(Job job) {
        if (!lastStatesFromFile.isEmpty() && lastStatesFromFile.containsKey(job)) {
            JobState lastState = lastStatesFromFile.get(job);
            if (lastState != null) {
                return lastState;
            }
        }
        return new JobState(JobState.Phase.COMPLETED, JobState.Result.NOT_BUILT);
    }

    /**
     * Store the given states.
     *
     * TODO: not working
     *
     * @param lastStates states to store
     */
    public void storeJobStates(Map<Job, JobState> lastStates) {
        try {
            JsonContainer container = new JsonContainer();
            container.setLastStates(lastStates);
            Files.write(storageJsonFile, new Gson().toJson(container).getBytes());
        } catch (IOException e) {
            logger.error("can't write last job states to " + storageJsonFile.getFileName().toString(), e);
        }
        readFile();
    }

    private void readFile() {
        try {
            InputStream in = Files.newInputStream(storageJsonFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JsonContainer jsonContainer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, JsonContainer.class);
            if (jsonContainer != null && jsonContainer.getLastStates() != null) {
                lastStatesFromFile = jsonContainer.getLastStates();
            } else {
                lastStatesFromFile = Maps.newLinkedHashMap();
            }
        } catch (IOException e) {
            logger.error("can't read job state storage", e);
            lastStatesFromFile = Maps.newLinkedHashMap();
        }
    }

    /**
     * Simple container model for JSON.
     */
    private class JsonContainer {

        private Map<Job, JobState> lastStates;

        private Map<Job, JobState> getLastStates() {
            return lastStates;
        }

        private void setLastStates(Map<Job, JobState> lastStates) {
            this.lastStates = lastStates;
        }
    }
}
