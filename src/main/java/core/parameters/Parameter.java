/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package core.parameters;


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
