package stes.isami.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * It contains the basic functions to work with a xml file.
 * It reads parameters definitions, tasks definitions.
 * Also, save new job and tasks definition to an xml file.
 */
public class XMLWorker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public XMLWorker() {

    }

    /**
     * Parse the xml file and return the document
     * @param filename the xml filename with the complete path
     * @return document parsed
     * @throws ParserConfigurationException if the file is corrupted
     * @throws IOException if the file cannot be read
     */
    public Document readFile(File filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        return db.parse(filename);
    }

    /**
     * Get the element describing the tasks
     * @param name Element to be parsed
     * @return element found.
     *         Null if the tasks cannot be found or the document is empty.
     */
    public Element getElementByName(Element element,String name) {

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equalsIgnoreCase(name)) {
                    return elem;
                }
            }
        }

        return null;

    }


    /**
     * Get the element describing the tasks
     * @param name Element to be parsed
     * @return element found.
     *         Null if the tasks cannot be found or the document is empty.
     */
    public List<Element> getElementsByName(Element element, String name) {
        List<Element> elements = new ArrayList<>();

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getTagName().equals(name)) {
                    elements.add(elem);
                }
            }
        }

        return elements;
    }

    /**
     * Get the value of the child element of the parent element {@code element}
     * @param element parent element
     * @param childName child tag name
     * @return value of the children
     */
    public String getChildrenValue(Element element,String childName) {
        Element child = getElementByName(element,childName);
        if (child == null) {
            return "";
        }

        if (child.getTextContent() == null) {
            return "";
        }

        return child.getTextContent();
    }

    /**
     * Get the options list of the list element. The options are defined as:
     * <pre
     *  <options>
     *      <option>Value</option>
     *      <options>value</options>
     *  </options>
     *  </pre
     * @param element parent element
     * @return value of the children
     */
    public List<String> getOptionsList(Element element) {

        List<String> stringList = new ArrayList<>();
        Element options = getElementByName(element,"options");
        if (options == null) {
            return stringList;
        }

        for (int i = 0; i < options.getChildNodes().getLength(); i++) {
            Node node = options.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getTagName().equals("option")) {
                    stringList.add(elem.getTextContent());
                }
            }
        }

        return stringList;
    }

    /**
     * Get a list of parameter elements for the job. It parses the document and it
     * returns a list containing all the elements defining a parameter. A parameter element
     * can be defined as following:
     * <pre>{@code
     *      <parameter name="isamiVersion" type="list">
     *           <description>Isami used version</description>
     *           <label>Isami version</label>
     *           <array>
     *              <string>v7.2.1</string>
     *              <string>v8.2.0</string>
     *          </array>
     *          <value>v7.2.1</value>
     *      </parameter>
     * }
     * </pre>
     * @param document Document to be parsed
     * @return List of parameter elements. Empty list if no parameter definition has been found in the document.
     * @throws NullPointerException if the <b>document</b>  is null
     */
    public ArrayList<Element> getJobParameterElements(Document document) throws NullPointerException {
        ArrayList<Element> parameterElements = new ArrayList<>();

        if (document == null) {
            throw new NullPointerException("Document null");
        }

        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if ( elem.getParentNode().getNodeName().equals("job")) {
                    if (elem.getNodeName().equals("parameter") || elem.getNodeName().equals("code")) {
                        parameterElements.add(elem);
                    }
                }
            }
        }

        return parameterElements;

    }



    /**********************************
     *
     *           PRIVATE
     *
     **********************************/




}
