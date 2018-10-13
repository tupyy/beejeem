package stes.isami.core.parameters;


import org.w3c.dom.Element;

import java.util.UUID;

/**
 * Parameter interface, represents parameters or variables used in the project
 */
public interface Parameter<ValueType> extends Cloneable {

    /**
     * Get paramter name
     * @return
     */
    public String getName();

    /**
     * Get the name used in the code template
     * @return
     */
    public String getLabel();

    /**
     * Set the label
     * @param label to set
     */
    public void setLabel(String label);

    /**
     * Get the parameter ID
     * @return
     */
    public UUID getID();

    /**
     * Get the description of the parameter
     * @return
     */
    public String getDescription();

    /**
     * Set the description
     * @param description to set
     */
    public void setDescription(String description);

    /**
     * Get the category
      * @return
     */
    public String getCategory();

    /**
     * Set the category
     * @param category to set
     */
    public void setCategory(String category);

    public ValueType getValue();

    /**
     * Set value to parameter
     * @param newValue
     */
    public void setValue (Object newValue);

    /**
     * Get the source of the parameter. It can be internal,external or file
     * @return the type of source which can update the parameter
     */
    public String getSource();

    public void loadValueFromXML(Element xmlElement);

    public void saveValueToXML(Element xmlElement);


    /**
     * Return the true if the parameter is valid (e.g value is set)
     */
    public boolean isValid();

    /**
     * Clone the parameter
     * @return the clone
     */
    Parameter<ValueType> clone();

}
