package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 14/03/2017.
 */
public final class JStesPreferences  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Holds the job definitions parameters
     */
    private final List<Element> jobDefinitions = new ArrayList<>();

    /**
     * Holds the job types
     */
    private final List<Element> jobTypes = new ArrayList<>();

    private final List<Element> jobs = new ArrayList<>();

    private final List<Element> userConfiguration = new ArrayList<>();

    public JStesPreferences() {

    }

    /**
     * Return the value of the key element
     * @return return the value or empty string if not defined
     */
    public String getUserConfValue(String key) {
        try {
            return getElement(userConfiguration,key).getNodeValue();
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

    /**
     * Set a new value to the element {@code key}
     * @param key
     * @param value
     */
    public void setUserConfValue(String key, String value) {
        try {
            Element element = getElement(userConfiguration,key);
            element.setNodeValue(value);
        }
        catch (IllegalArgumentException ex) {
            ;
        }
    }

    /**
     * Get the job types
     * @return list of job types
     */
    public List<String> getJobTypes() {
        List<String> retJobTypes = new ArrayList<>();
        for (Element element: jobTypes) {
            try {
                Element jobTypeName = getChildrenElement(element,"name");
                retJobTypes.add(jobTypeName.getNodeValue());
            }
            catch (NullPointerException e) {}

        }

        return retJobTypes;
    }

    /**
     * Get the job names of type {@code jobTypeName}
     * @param jobTypeName
     * @return
     */
    public List<String> getJobNames(String jobTypeName) {

        List<String> retJobNames = new ArrayList<>();

        for (Element element: jobs) {
            if (element.getTagName().equals(jobTypeName)) {
                NodeList nodeList = element.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element elem = (Element) node;
                        if (elem.getTagName().equals(jobTypeName)) {
                            return elem;
                        }
                    }
                }
            }

        }

        return retJobTypes;
    }

    /**
     * Get element
     * @param elements
     * @param elementName
     * @return
     */
    private Element getElement(List<Element> elements, String elementName) throws IllegalArgumentException{
        for(Element element: elements) {
            if (element.getTagName().equals(elementName)) {
                return element;
            }
        }

        throw new IllegalArgumentException("Not found");
    }

    /**
     * Get children from element
     * @param element
     * @param childName
     * @return
     * @throws NullPointerException
     */
    private Element getChildrenElement(Element element,String childName) throws NullPointerException {

        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals(childName)) {
                    return elem;
                }
            }
        }

        throw new NullPointerException();
    }

}
