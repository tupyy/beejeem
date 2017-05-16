package configuration;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.CodeParameter;
import core.parameters.parametertypes.StringParameter;
import core.util.XMLWorker;
import eventbus.*;
import main.JStesCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read the configuration file
 */
public class JStesConfiguration extends AbstractComponentEventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static JStesPreferences preferences = new JStesPreferences();

    private File configurationFile;

    public JStesConfiguration() {

    }

    /**
     * Get preferences
     * @return
     */
    public static Preferences getPreferences() {
        return preferences;
    }

    /**
     * Load the configuration file
     * @param file
     * @throws IOException
     */
    public void loadConfiguration(File file) throws IOException {

        if (file.exists() && file.canRead()) {
            configurationFile = file;
            if (file.getName().endsWith("xml")) {
                XmlConfigurationReader xmlConfigurationReader = new XmlConfigurationReader();
                try {
                    preferences.setJobDefinitions(xmlConfigurationReader.getJobDefintions(file));
                    preferences.setConfigurationSet(xmlConfigurationReader.getUserConfiguration(file));
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onComponentAction(ComponentAction event) {
        switch (event.getAction()) {
            case PREFERENCES_SAVED:
                if (configurationFile.canWrite()) {
                    if (configurationFile.getName().endsWith("xml")) {
                        XmlConfigurationWriter xmlConfigurationWriter = new XmlConfigurationWriter();
                        try {
                            xmlConfigurationWriter.saveToFile(configurationFile,preferences);
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    }

    private void addFolderParameters() {

    }
}
