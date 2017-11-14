package net.twasi.core.plugin.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.plugin.PluginConfig;
import net.twasi.core.plugin.api.TwasiPlugin;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class JavaPluginLoader {

    public TwasiPlugin plugin;

    public JavaPluginLoader (File file) {
        try {
            URL[] urls = {new URL("jar:file:" + file.getAbsolutePath() + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            // Check for plugin.yml
            URL infoYamlUrl = cl.getResource("plugin.yml");
            if (infoYamlUrl == null) {
                throw new Exception("Cannot load plugin " + file.getAbsolutePath() + ": Invalid or non-existent plugin.yml");
            }

            // Get File, read
            InputStream stream = (InputStream) infoYamlUrl.getContent();
            PluginConfig config = null;

            // Parse
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            try {
                config = mapper.readValue(stream, PluginConfig.class);
            } catch (Exception ee) {
                TwasiLogger.log.error("Cannot parse config file: " + ee.getMessage());
            }

            Class<?> jarClass;
            try {
                jarClass = Class.forName(config.main, true, cl);
            } catch (ClassNotFoundException ex) {
                throw new Exception("Cannot find main class `" + config.main + "'", ex);
            }

            Class<? extends TwasiPlugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(TwasiPlugin.class);
            } catch (ClassCastException ex) {
                throw new Exception("Main class `" + config.main + "' does not extend TwasiPlugin", ex);
            }

            plugin = pluginClass.newInstance();
            plugin.setConfig(config);
        } catch (Exception e) {
            TwasiLogger.log.error(e);
            e.printStackTrace();
        }
    }

}