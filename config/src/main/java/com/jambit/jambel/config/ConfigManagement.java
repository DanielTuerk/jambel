package com.jambit.jambel.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jambit.jambel.config.jambel.JambelConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
@Component
public class ConfigManagement {

    private final String configFilePath;

    private final Map<Path, JambelConfiguration> jambelConfigurations = Maps.newHashMap();

    private final List<ConfigListener> listeners = Lists.newArrayList();

    @Autowired
    public ConfigManagement(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void addListener(ConfigListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConfigListener listener) {
        listeners.remove(listener);
    }

    @PostConstruct
    public void initConfigFromFilePath() {
        Path path = Paths.get(configFilePath);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.json")) {
            for (Path p : ds) {
                // Iterate over the paths in the directory and print filenames
                System.out.println(p.getFileName());


                try (InputStream in = Files.newInputStream(p);
                     BufferedReader reader =
                             new BufferedReader(new InputStreamReader(in))) {

                    JambelConfiguration jambelConfiguration = new Gson().fromJson(reader, JambelConfiguration.class);
                    jambelConfigurations.put(p, jambelConfiguration);
                } catch (IOException x) {
                    System.err.println(x);
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map<Path, JambelConfiguration> getJambelConfigurations() {
        return jambelConfigurations;
    }

}
