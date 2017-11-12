package net.twasi.core.plugin;

import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.plugin.api.TwasiPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginManager {

    private List<TwasiPlugin> plugins = new ArrayList<>();

    public boolean registerPlugin(TwasiPlugin plugin) {
        if (plugins.contains(plugin)) {
            TwasiLogger.log.info("Tried to register plugin " + plugin.getConfig().getName() + " twice. Skipped.");
            return false;
        }

        plugins.add(plugin);

        plugin.onEnable();
        return true;
    }

    public List<TwasiPlugin> getPlugins() {
        return plugins;
    }

    public List<TwasiPlugin> getByCommand(String command) {
        List<TwasiPlugin> availablePlugins = plugins.stream().filter(plugin -> plugin.getConfig().getCommands().contains(command)).collect(Collectors.toList());
        return availablePlugins;
    }
}
