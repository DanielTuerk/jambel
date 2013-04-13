package com.jambit.jambel.config;

import com.google.common.collect.Maps;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manager bean to handle the JSON configuration files in the file system.
 * <ul>
 * <li>start monitoring at start up</li>
 * <li>stop monitoring on destroy</li>
 * </ul>
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Component
public class ConfigManagement {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManagement.class);

    /**
     * Path of the directory for the configuration files.
     */
    private final String configFilePath;

    /**
     * Executor to monitor the configuration directory for modifications.
     */
    private final ScheduledExecutorService configFilesWatchServiceExecutor;

    @Autowired
    public ConfigManagement(String configFilePath, ScheduledExecutorService configFilesWatchServiceExecutor) {
        this.configFilePath = configFilePath;
        this.configFilesWatchServiceExecutor = configFilesWatchServiceExecutor;
    }

    @PostConstruct
    public void init() {
        // schedule the watch service in repeating loop to look for modification at the jambel configuration files
        configFilesWatchServiceExecutor.scheduleAtFixedRate(new ConfigPathWatchService(configFilePath), 0L, 1L, TimeUnit.MILLISECONDS);
    }

    /**
     * Read the JSON configuration files from the configured file path.
     * The JSON content is parsed to {@link JambelConfiguration} model and mapped to the JSON file name.
     *
     * @return {@link Map<Path,JambelConfiguration>} jambel configurations
     */
    public Map<Path, JambelConfiguration> loadConfigFromFilePath() {
        Map<Path, JambelConfiguration> jambelConfigurations = Maps.newHashMap();
        Path path = Paths.get(configFilePath);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.json")) {
            for (Path p : ds) {
                try {
                    JambelConfiguration jambelConfiguration = ConfigUtils.loadConfigFromPath(p);
                    if (jambelConfiguration != null) {
                        jambelConfigurations.put(p.getFileName(), jambelConfiguration);
                    } else {
                        logger.error("can't load jambel from config file: " + p.getFileName());
                    }
                } catch (IOException e1) {
                    logger.error("can't load jambel config", e1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("can't load configurations from " + configFilePath, e);
        }
        return jambelConfigurations;
    }

    @PreDestroy
    public void destroy() {
        configFilesWatchServiceExecutor.shutdownNow();
    }

}
