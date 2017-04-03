package core.plugin;

import core.CoreEngine;
import core.creator.Creator;
import core.modules.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Supplier;

/**
 * Created by cosmin on 01/04/2017.
 */
public class PluginLoader implements Supplier<Boolean>{

    private final String folderPath;
    private ServiceLoader<Plugin> loader;
    private final Logger logger = LoggerFactory.getLogger(CoreEngine.class);

    private List<Plugin> initializedPlugins = new ArrayList<>();

    public PluginLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    public Boolean get() {
        try {
            loadPlugins(folderPath);
            return Boolean.TRUE;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return Boolean.FALSE;
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
                initializedPlugins.add(plugin);
                logger.info("Plugin loaded: {}",plugin.getName());

            }
        } catch (ServiceConfigurationError serviceError) {}
    }

    /**
     * Returns the instance of a module of given class
     */
    @SuppressWarnings("unchecked")
    public  List<Plugin> getPlugins() {
        return initializedPlugins;
    }

}
