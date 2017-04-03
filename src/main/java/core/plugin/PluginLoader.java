package core.plugin;

import core.CoreEngine;
import core.modules.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by cosmin on 01/04/2017.
 */
public class PluginLoader implements Runnable{

    private final String folderPath;
    private ServiceLoader<Plugin> loader;
    private final Logger logger = LoggerFactory.getLogger(CoreEngine.class);

    private static Map<String, Plugin> initializedPlugins = Collections
            .synchronizedMap(new Hashtable<>());

    public PluginLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    public void run() {
        try {
            loadPlugins(folderPath);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void loadPlugins(String folderPath) {
        File pluginsDir = new File(folderPath);
        File[] jlist = pluginsDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getPath().toLowerCase().endsWith(".jar");
            }
        });

        URL[] urls = new URL[jlist.length];
        for (int i = 0; i < jlist.length; i++)
            try {
                logger.info("Plugin file: {}",jlist[i]);
                urls[i] = jlist[i].toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        URLClassLoader ucl = new URLClassLoader(urls);
        loader = ServiceLoader.load(Plugin.class, ucl);

        try {
            Iterator<Plugin> plugins = loader.iterator();
            while (plugins.hasNext()) {
                Plugin plugin = plugins.next();
                initializedPlugins.put(plugin.getName(),plugin);
                logger.info("Plugin loaded: {}",plugin.getName());

            }
        } catch (ServiceConfigurationError serviceError) {}
    }

    /**
     * Returns the instance of a module of given class
     */
    @SuppressWarnings("unchecked")
    public static Plugin getPlugin(String pluginClass) {
        return initializedPlugins.get(pluginClass);
    }

    public static Module getModule(String moduleName) {
        for(Map.Entry<String,Plugin> entry: initializedPlugins.entrySet()) {
            Plugin plugin = entry.getValue();
            Module module = plugin.getModule(moduleName);
            if (module != null) {
                return module;
            }
        }

        return null;
    }
}
