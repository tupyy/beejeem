package core.util;

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

/**
 * It contains the basic functions to work with a xml file.
 * It reads parameters definitions, tasks definitions.
 * Also, save new job and tasks definition to an xml file.
 */
public class XMLWorker {


    public XMLWorker() {

    }

    /**
     * Parse the xml file and return the document
     * @param filename the xml filename with the complete path
     * @return document parsed
     * @throws ParserConfigurationException if the file is corrupted
     * @throws IOException if the file cannot be read
     */
    public Document readFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        return db.parse(new File(filename));
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
                if (elem.getNodeName().equals(name)) {
                    return elem;
                }
            }
        }

        return null;

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


    /**
     * Count the elements in the document
     * @param document
     * @return the number of element {@code<parameter>} and {@code<code>} found in the document
     */
    public int countElements(Document document) {
        int count = 0;

        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if ( elem.getParentNode().getNodeName().equals("job")) {
                    if (elem.getNodeName().equals("parameter") || elem.getNodeName().equals("code")) {
                        count++;
                    }
                }
            }
        }

        return count;

    }

    /**
     * Return an attribute of the job
     * @param document xml job definition document
     * @param attributeName
     * @return
     */

    public String getJobAttribute(Document document,String attributeName) {

        if (document == null) {
            return "";
        }

        if (attributeName.isEmpty()) {
            return "";
        }

        return document.getDocumentElement().getAttribute(attributeName);
    }

    /**********************************
     *
     *           PRIVATE
     *
     **********************************/

    private boolean isValid(Element element) {
        if (element.getAttribute("name").isEmpty() || element.getAttribute("type").isEmpty() ) {
            return false;
        }

        return true;
    }



}
