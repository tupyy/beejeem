package main.configuration;

import core.parameters.ParameterSet;
import core.parameters.parametertypes.Aircraft;
import core.parameters.parametertypes.AircraftParameter;
import core.parameters.parametertypes.CodeParameter;
import core.parameters.parametertypes.StringParameter;
import core.util.XMLWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read the configuration file
 */
public class JStesConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static JStesPreferences preferences = new JStesPreferences();

    public JStesConfiguration() {

    }

    /**
     * Get preferences
     * @return
     */
    public static JStesPreferences getPreferences() {
        return preferences;
    }

    public void loadConfiguration(File file) throws IOException {
        XMLWorker xmlWorker = new XMLWorker();

        try {
            Document confDocument = xmlWorker.readFile(file);
            ParameterSet userConfiguration = new ParameterSet();

            //get user configuration
            Element user = xmlWorker.getElementByName(confDocument.getDocumentElement(),"user");
            if (user != null) {
                for (int i = 0; i < user.getChildNodes().getLength(); i++) {
                    Node node = user.getChildNodes().item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) node;
                        StringParameter stringParameter = new StringParameter(elem.getTagName(), elem.getTagName(), "User configuration");
                        stringParameter.setValue(elem.getTextContent());
                        userConfiguration.addParameter(stringParameter);
                    }
                }
            }
            preferences.setUserConfiguration(userConfiguration);

            //get the jobs element
            Element jobs = xmlWorker.getElementByName(confDocument.getDocumentElement(),"jobs");
            if (jobs != null) {
                List<Element> jobDefintionElements = xmlWorker.getElementsByName(jobs,"job");

                for (Element element: jobDefintionElements) {
                    JobDefinition jobDefinition = new JobDefinition();
                    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                        Node node = element.getChildNodes().item(i);

                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) node;
                            if (elem.getTagName().equals("file")) {
                                readJobDefinitionFile(elem.getTextContent(),jobDefinition);
                            }
                            else {
                                StringParameter stringParameter = new StringParameter(elem.getTagName(), elem.getTagName(), "Job definition");
                                stringParameter.setValue(elem.getTextContent());
                                jobDefinition.getParameters().addParameter(stringParameter);
                            }
                        }

                    }
                    preferences.addJobDefition(jobDefinition);

                }

            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the xml file which define the job and write the parameter set and module names
     * to the {@link JobDefinition}
     * @param nodeValue
     * @param jobDefinition
     */
    private void readJobDefinitionFile(String nodeValue, JobDefinition jobDefinition) throws IOException, ParserConfigurationException, SAXException,IllegalArgumentException {

        try {
            File confFile = new File(JStesConfiguration.class.getClassLoader().getResource(nodeValue).getFile());

                XMLWorker xmlWorker = new XMLWorker();
                Document document = xmlWorker.readFile(confFile);

                Element jobDefinitionElement = document.getDocumentElement();

                Element parameters = xmlWorker.getElementByName(jobDefinitionElement, "parameters");
                if (parameters == null) {
                    logger.error("Parameters element is not found in the job definition element");
                    throw new IllegalArgumentException("Parameters element is not found in the job definition element");
                }

                Element code = xmlWorker.getElementByName(jobDefinitionElement, "code");
                if (code == null) {
                    logger.error("Code element is not found in the job definition element");
                    throw new IllegalArgumentException("Code element is not found in the job definition element");
                }

                Element modules = xmlWorker.getElementByName(jobDefinitionElement, "modules");
                if (modules == null) {
                    logger.error("Module element is not found in the job definition element");
                    throw new IllegalArgumentException("Module element is not found in the job definition element");
                }

                ParameterSet newSet = createParameters(parameters);
                newSet.addParameter(createCodeParameter(code));
                jobDefinition.getParameters().addParameters(newSet);
                jobDefinition.setModuleManagers(createModuleParameter(modules));
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Create a parameter set from a list of parameter elements
     * @param parameters {@code ArrayList} of elements defining the parameters
     */
    private ParameterSet createParameters(Element parameters) {

        ParameterSet parameterSet = new ParameterSet();

        ArrayList<Element> parametersList = new ArrayList<>();

        NodeList nodeList = parameters.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals("parameter")) {
                    parametersList.add(elem);
                }
            }
        }
        parameterSet.loadDefinitionFromXML(parametersList);

        return parameterSet;
    }

    /**
     * Create the code parameter
     * @param code element defining the element. The element has been checked.
     */
    private CodeParameter createCodeParameter(Element code) {

        CodeParameter codeParameter = new CodeParameter("pythonCode");
        NodeList nodeList = code.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals("value")) {
                    codeParameter.setValue(elem.getTextContent());
                }
            }
        }

        return codeParameter;
    }

    /**
     * Return a child element with name
     * @param parentElement
     * @param name
     * @return null if don't found
     */
    private Element getElement(Element parentElement, java.lang.String name) {
        NodeList nodeList = parentElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals(name)) {
                    return elem;
                }
            }
        }

        return null;
    }

    /**
     * Create the module parameters
     * @param modulesElement
     */
    private List<Element> createModuleParameter(Element modulesElement) {
        List<Element> moduleSet = new ArrayList<>();

        NodeList nodeList = modulesElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals("module")) {
                    moduleSet.add(elem);
                }
            }
        }

        return moduleSet;
    }
}
