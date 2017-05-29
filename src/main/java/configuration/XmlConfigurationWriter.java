package configuration;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by cosmin on 02/05/2017.
 */
public class XmlConfigurationWriter implements ConfigurationWriter {

    @Override
    public void saveToFile(File file, Preferences preferences) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("configuration");
        doc.appendChild(rootElement);

        Element userElement = doc.createElement("user");
        rootElement.appendChild(userElement);
        for (Property property: preferences.getProperties()) {
            Element element = doc.createElement(property.getName());
            if (property instanceof SimpleStringProperty) {
                element.appendChild(doc.createTextNode((String) property.getValue()));
            }
            else if (property instanceof SimpleBooleanProperty) {
                element.appendChild(doc.createTextNode(property.getValue().toString()));
            }
            userElement.appendChild(element);
        }

        Element jobElement = doc.createElement("jobs");
        rootElement.appendChild(jobElement);
        for (JobDefinition jobDefinition: preferences.getJobDefinitions()) {
            Element element = doc.createElement("job");
            element.appendChild(doc.createTextNode(jobDefinition.getFile()));
            jobElement.appendChild(element);
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);

    }
}
