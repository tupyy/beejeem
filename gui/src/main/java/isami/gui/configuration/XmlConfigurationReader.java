package stes.isami.bjm.configuration;

import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.parameters.parametertypes.CodeParameter;
import stes.isami.core.parameters.parametertypes.StringParameter;
import stes.isami.core.util.XMLWorker;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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
 * Read the configuration from a xml file
 */
public class XmlConfigurationReader implements ConfigurationReader {

    private final String JOBS_TAG = "jobs";
    private final String JOB_DEF_TAG ="job";
    private final String PARAMETERS_TAG="parameters";
    private final String CODE_TAG = "code";
    private final String MODULE_TAG = "module";
    private final String MODULES_TAG = "modules";
    private final String PYTHON_TAG = "pythonCode";

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public XmlConfigurationReader() {

    }

    @Override
    public List getUserConfiguration(File file) throws ParserConfigurationException, SAXException, IOException {
        Document doc = getDocument(file);
        if (doc != null) {
            return readConfigurationBlock(doc,"user");
        }

        return new ArrayList();
    }

    @Override
    public List getJobDefintions(File file) throws ParserConfigurationException, SAXException, IOException {

        List<JobDefinition> jobDefinitions = new ArrayList<>();
        Document doc = getDocument(file);
        if (doc != null) {
            XMLWorker xmlWorker = new XMLWorker();
            //get the jobs element
            Element jobs = xmlWorker.getElementByName(doc.getDocumentElement(), JOBS_TAG);
            if (jobs != null) {
                List<Element> jobDefinitionElements = xmlWorker.getElementsByName(jobs, JOB_DEF_TAG);

                for (Element element : jobDefinitionElements) {
                    try {
                        JobDefinition jobDefinition  = readJobDefinitionFile(element.getTextContent());
                        if (jobDefinition != null) {
                            jobDefinitions.add(jobDefinition);
                        } else {
                            logger.error("Job definition null for {}", element.getTextContent());
                        }
                    } catch (IllegalArgumentException | IOException ex) {
                        logger.error(ex.getMessage());
                    } catch (ParserConfigurationException e) {
                        logger.error(e.getMessage());
                    } catch (SAXException e) {
                        logger.error(e.getMessage());
                    }

                }
            }
        }

        return jobDefinitions;
    }

    /**
     * Parse the xml file and return the document
     * @param file
     * @return
     */
    private Document getDocument(File file) throws IOException, SAXException, ParserConfigurationException {
        XMLWorker xmlWorker = new XMLWorker();

        Document confDocument = null;
        confDocument = xmlWorker.readFile(file);
        return confDocument;


    }
    /**
     * Parse the xml file which define the job and write the parameter set and module names
     * to the {@link JobDefinition}
     * @param filename
     * @return the definition of job
     */
    private JobDefinition readJobDefinitionFile(String filename) throws IOException, ParserConfigurationException, SAXException,IllegalArgumentException {

        JobDefinition jobDefinition = new JobDefinition();
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
        }
        catch (IllegalArgumentException ex) {
            return null;
        }

        newSet.addParameter(createCodeParameter(code));
        jobDefinition.getParameters().addParameters(newSet);
        jobDefinition.setModuleElements(createModuleParameter(modules));
        jobDefinition.setFile(filename);

        return jobDefinition;

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

    private List<Property> readConfigurationBlock(Document confDocument, String blockname) {

        List<Property> properties = new ArrayList<>();
        XMLWorker xmlWorker = new XMLWorker();

        //get user configuration
        Element user = xmlWorker.getElementByName(confDocument.getDocumentElement(),blockname);
        if (user != null) {
            for (int i = 0; i < user.getChildNodes().getLength(); i++) {
                Node node = user.getChildNodes().item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    Property property;
                    if (elem.getTextContent().equalsIgnoreCase("true") || elem.getTextContent().equalsIgnoreCase("false")) {
                       property = new SimpleBooleanProperty(elem.getTagName(),elem.getTagName(),Boolean.valueOf(elem.getTextContent()));
                    }
                    else {
                        property = new SimpleStringProperty(elem.getTagName(), elem.getTagName(), elem.getTextContent());

                    }
                    properties.add(property);
                }
            }
        }

        return properties;
    }

}
