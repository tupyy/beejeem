package stes.isami.core.creator;

import stes.isami.core.plugin.Plugin;
import stes.isami.core.plugin.PluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class factory for the creators.
 * <p>It keeps all the loaded creators in a {@code Map<ClassName,CreatorInstance>}</p>
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

    /**
     * Load the {@link Creator} classes from a {@link Plugin}
     * @param pluginLoader
     */
    public void loadCreators(PluginLoader pluginLoader) {
        createCreatorList(pluginLoader);
    }

    /**
     * Get the creator for the type job {@code className}
     * @param className
     * @return initialized creator or null if not found
     */
    public static Creator getCreator(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        if (initializedCreators.containsKey(className)) {
            Creator creatorInstance = initializedCreators.get(className).getClass().newInstance();
            return creatorInstance;
        }
        else {
            throw new ClassNotFoundException("No creator loaded for "+className);
        }


    }

    private void createCreatorList(PluginLoader pluginLoader) {
        for(Plugin plugin: pluginLoader.getPlugins()) {
            for(Creator creator : plugin.getCreators()) {
                logger.info("Creator loaded: {}", creator.getClass().getName());
                if (creator != null) {
                    initializedCreators.put(creator.getClass().getName(), creator);
                }
            }
        }
    }

}
