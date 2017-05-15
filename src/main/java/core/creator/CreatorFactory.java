package core.creator;

import core.CoreEngine;
import core.modules.Module;
import core.plugin.Plugin;
import core.plugin.PluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by tctupangiu on 03/04/2017.
 */
public class CreatorFactory {

    private static Map<String, Creator> initializedCreators = Collections
            .synchronizedMap(new Hashtable<>());

    private final Logger logger = LoggerFactory.getLogger(CreatorFactory.class);

    public CreatorFactory(PluginLoader pluginLoader) {
        createCreatorList(pluginLoader);
    }

    public CreatorFactory() {

    }

    public void loadCreators(PluginLoader pluginLoader) {
        createCreatorList(pluginLoader);
    }

    /**
     * Load a plugin
     * @param plugin
     */
    public static void loadPlugin(Plugin plugin) {
        Creator creator = plugin.getCreator();
        if (creator != null) {
            initializedCreators.put(creator.getJobType(), creator);
        }
    }

    /**
     * Get the creator for the type job {@code jobType}
     * @param className
     * @return initialized creator or null if not found
     */
    public static Creator getCreator(String className) {
        return initializedCreators.get(className);
    }

    private void createCreatorList(PluginLoader pluginLoader) {
        for(Plugin plugin: pluginLoader.getPlugins()) {
            Creator creator = plugin.getCreator();
            logger.info("Creator loaded: {}",creator.getClass().getName());
            if (creator != null) {
                initializedCreators.put(creator.getClass().getName(), creator);
            }
        }
    }

    /**
     * Initialized SimpleCreator
     */
    private void initializeSimpleCreators() {

    }
}
