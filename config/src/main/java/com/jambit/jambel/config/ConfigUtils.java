package com.jambit.jambel.config;

import com.google.gson.Gson;
import com.jambit.jambel.config.jambel.JambelConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Helper class for the config module.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class ConfigUtils {

    /**
     * Parse the given JSON file to the {@link JambelConfiguration}.
     *
     * @param p {@link Path} to read the file content
     * @return {@link JambelConfiguration} or <code>null</code>
     * @throws IOException
     */
    public static JambelConfiguration loadConfigFromPath(Path p) throws IOException {
        InputStream in = Files.newInputStream(p);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return new Gson().fromJson(reader, JambelConfiguration.class);
    }
}
