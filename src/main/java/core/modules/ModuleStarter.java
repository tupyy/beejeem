package core.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * Create an instance of the module based on the name of the module
 */
public class ModuleStarter implements Runnable{

    private static final File MODULES_FILE = new File("resources/modules.xml");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private static Map<Class<? extends Module>, Module> initializedModules = Collections
            .synchronizedMap(new Hashtable<>());

    @Override
    public void run() {

        logger.info("Loading modules");

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            Document modulesDocument = null;
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            modulesDocument = dBuilder.parse(MODULES_FILE);
            Element rootElement = modulesDocument.getDocumentElement();
            NodeList moduleNodes = rootElement.getChildNodes();
            for (int i = 0; i < moduleNodes.getLength(); i++) {
                Node moduleNode = moduleNodes.item(i);
                if (moduleNode.getNodeName() != "module")
                    continue;
                String moduleClassName = moduleNode.getTextContent();
                try {

                    // Start up the module
                    startModule(moduleClassName);

                } catch (Exception e) {
                    logger.warn("Failed to initialize module class "
                            + moduleClassName + ": " + e);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            logger.error("Could not load modules from " + MODULES_FILE);
            System.exit(1);
        }

//        try {
//            logger.info("Loading configuration");
//            MZmineCore.getConfiguration()
//                    .loadConfiguration(MZmineConfiguration.CONFIG_FILE);
//        } catch (Exception e) {
//            logger.error("Could not load configuration from "
//                    + MZmineConfiguration.CONFIG_FILE);
//        }

    }

    /**
     * Start module
     * @param moduleClassName
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private void startModule(final String moduleClassName)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {

        logger.info("Loading module class " + moduleClassName);

        // Create an instance of the module
        @SuppressWarnings("unchecked")
        Class<? extends Module> moduleClass = (Class<? extends Module>) Class
                .forName(moduleClassName);
        Module newModule = moduleClass.newInstance();
        initializedModules.put(moduleClass, newModule);
    }

    /**
     * Returns the instance of a module of given class
     */
    @SuppressWarnings("unchecked")
    public static <ModuleType extends Module> ModuleType getModuleInstance(
            Class<ModuleType> moduleClass) {
        return (ModuleType) initializedModules.get(moduleClass);
    }
}
