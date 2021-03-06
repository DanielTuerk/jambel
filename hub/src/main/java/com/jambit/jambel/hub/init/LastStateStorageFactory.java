package com.jambit.jambel.hub.init;

import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Factory to create the {@link LastStateStorage} for the hostname of a {@link SignalLightConfiguration}.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Component
public class LastStateStorageFactory {

    /**
     * Root file path for the storage.
     */
    private final String storageFilePath;

    @Autowired
    public LastStateStorageFactory(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    @PostConstruct
    public void init() {
        // create the config path if not existing
        if (!Files.isDirectory(Paths.get(storageFilePath))) {
            try {
                Files.createDirectory(Paths.get(storageFilePath));
            } catch (IOException e) {
                throw new RuntimeException("can't create the folder for the last state storage", e);
            }
        }
    }

    /**
     * Create a new instance of the {@link LastStateStorage} for the given {@link SignalLightConfiguration}
     *
     * @param signalLightConfiguration {@link SignalLightConfiguration}
     * @return {@link LastStateStorage}
     */
    public LastStateStorage createStorage(SignalLightConfiguration signalLightConfiguration) {
        Path filePath = Paths.get(storageFilePath + signalLightConfiguration.getHostAndPort().getHostText() + ".json");
        try {
            Files.createFile(filePath);
        } catch (FileAlreadyExistsException x) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException("can't create job state storage file", e);
        }
        return new LastStateStorage(filePath);

    }
}
