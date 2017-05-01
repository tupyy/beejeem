package configuration;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
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

    private final String JOBS_TAG = "jobs";
    private final String JOB_DEF_TAG ="job";
    private final String PARAMETERS_TAG="parameters";
    private final String CODE_TAG = "code";
    private final String MODULE_TAG = "module";
    private final String MODULES_TAG = "modules";
    private final String PYTHON_TAG = "pythonCode";

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

    /**
     * Load the configuration file
     * @param file
     * @throws IOException
     */
    public void loadConfiguration(File file) throws IOException {
        XMLWorker xmlWorker = new XMLWorker();

        Document confDocument = null;
        try {
            confDocument = xmlWorker.readFile(file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ParameterSet userConfiguration = readConfigurationBlock(confDocument,"user");
        preferences.setUserConfiguration(userConfiguration);

        //get the jobs element
        Element jobs = xmlWorker.getElementByName(confDocument.getDocumentElement(),JOBS_TAG);
        if (jobs != null) {
            List<Element> jobDefinitionElements = xmlWorker.getElementsByName(jobs,JOB_DEF_TAG);

            for (Element element: jobDefinitionElements) {
                JobDefinition jobDefinition = new JobDefinition();

                try {
                    if (readJobDefinitionFile(element.getTextContent(), jobDefinition)) {
                        preferences.addJobDefition(jobDefinition);
                    }
                    else {
                        logger.error("The parameter set from {} is invalid.",element.getTextContent());
                    }
                } catch (IllegalArgumentException ex) {
                    logger.error(ex.getMessage());
                } catch (ParserConfigurationException e) {
                    logger.error(e.getMessage());
                } catch (SAXException e) {
                    logger.error(e.getMessage());
                }

            }

        }

    }

    /**
     * Parse the xml file which define the job and write the parameter set and module names
     * to the {@link JobDefinition}
     * @param filename
     * @param jobDefinition
     */
    private boolean readJobDefinitionFile(String filename, JobDefinition jobDefinition) throws IOException, ParserConfigurationException, SAXException,IllegalArgumentException {

        File confFile = new File(filename);

        XMLWorker xmlWorker = new XMLWorker();
        Document document = xmlWorker.readFile(confFile);

        Element jobDefinitionElement = document.getDocumentElement();

        Element parameters = xmlWorker.getElementByName(jobDefinitionElement, PARAMETERS_TAG);
        if (parameters == null) {
            logger.error("Parameters element is not found in the job definition element");
            throw new IllegalArgumentException("Parameters element is not found in the job definition element");
        }

        Element code = xmlWorker.getElementByName(jobDefinitionElement, CODE_TAG);
        if (code == null) {
            logger.error("Code element is not found in the job definition element");
            throw new IllegalArgumentException("Code element is not found in the job definition element");
        }

        Element modules = xmlWorker.getElementByName(jobDefinitionElement, MODULES_TAG);
        if (modules == null) {
            logger.error("Module element is not found in the job definition element");
            throw new IllegalArgumentException("Module element is not found in the job definition element");
        }

        ParameterSet newSet = createParameters(parameters);
        try {
            StringParameter name = newSet.getParameter("name");

            if ( !checkValue(newSet.getParameter("destinationFolder"))) {
                return false;
            }
        }
        catch (IllegalArgumentException ex) {
            return false;
        }

        newSet.addParameter(createCodeParameter(code));
        jobDefinition.getParameters().addParameters(newSet);
        jobDefinition.setModuleElements(createModuleParameter(modules));

        return true;

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

        CodeParameter codeParameter = new CodeParameter(PYTHON_TAG);
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
                if (elem.getNodeName().equals(MODULE_TAG)) {
                    moduleSet.add(elem);
                }
            }
        }

        return moduleSet;
    }


    private boolean checkValue(Parameter p) {
         if (p.getValue().toString().isEmpty()) {
            return false;
        }

        return true;

    }

    private ParameterSet readConfigurationBlock(Document confDocument,String blockname) {
        ParameterSet blockParameterSet = new ParameterSet();
        XMLWorker xmlWorker = new XMLWorker();

        //get user configuration
        Element user = xmlWorker.getElementByName(confDocument.getDocumentElement(),blockname);
        if (user != null) {
            for (int i = 0; i < user.getChildNodes().getLength(); i++) {
                Node node = user.getChildNodes().item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    StringParameter stringParameter = new StringParameter(elem.getTagName(), elem.getTagName(), blockname);
                    stringParameter.setValue(elem.getTextContent());
                    blockParameterSet.addParameter(stringParameter);
                }
            }
        }

        return blockParameterSet;
    }
}
