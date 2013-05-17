package com.jambit.jambel.hub.init;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage for an jambel to persist the last build states.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class LastStateStorage {

    private static final Logger logger = LoggerFactory.getLogger(LastStateStorage.class.getName());

    private Map<Job, JobState> lastStatesFromFile = new HashMap<>();

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
     * @param lastStates states to store
     */
    public void storeJobStates(Map<Job, JobState> lastStates) {
        List<JsonContainer> jsonContainerList = new ArrayList<>();
        for (Map.Entry<Job, JobState> state : lastStates.entrySet()) {
            jsonContainerList.add(new JsonContainer(state.getKey(), state.getValue()));
        }
        try {
            Files.write(storageJsonFile, new Gson().toJson(jsonContainerList).getBytes());
        } catch (IOException e) {
            logger.error("can't write last job states to " + storageJsonFile.getFileName().toString(), e);
        }
    }

    /**
     * Read the storage file and marshal the stored {@link JobState}s of the {@link Job}s.
     *
     * Empty file or errors during execution will create a empty {@see lastStatesFromFile}.
     */
    private void readFile() {
        try {
            InputStream in = Files.newInputStream(storageJsonFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JsonContainer[] jsonContainer = new Gson().fromJson(reader, JsonContainer[].class);
            lastStatesFromFile = new HashMap<>();
            if (jsonContainer != null && jsonContainer.length > 0) {
                for (JsonContainer jsonEntry : jsonContainer) {
                    lastStatesFromFile.put(jsonEntry.job, jsonEntry.jobState);
                }
            }
        } catch (Exception e) {
            logger.error("can't read job state storage", e);
            lastStatesFromFile = new HashMap<>();
        }
    }

    /**
     * Simple container model for JSON.
     */
    private class JsonContainer {

        public Job job;
        public JobState jobState;

        public JsonContainer(Job job, JobState jobState) {
            this.job = job;
            this.jobState = jobState;
        }
    }
}
