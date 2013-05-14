package com.jambit.jambel.hub.init;

import com.jambit.jambel.config.jambel.SignalLightConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * Create a new instance of the {@link LastStateStorage} for the given {@link SignalLightConfiguration}
     *
     * @param signalLightConfiguration {@link SignalLightConfiguration}
     * @return {@link LastStateStorage}
     */
    public LastStateStorage createStorage(SignalLightConfiguration signalLightConfiguration) {
        Path filePath = Paths.get(storageFilePath + signalLightConfiguration.getHostAndPort().getHostText() + ".json");
        try {
            if (Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (FileAlreadyExistsException x) {
            //ignore
        } catch (IOException e) {
            throw new RuntimeException("can't create job state storage file", e);
        }
        return new LastStateStorage(filePath);

    }
}
