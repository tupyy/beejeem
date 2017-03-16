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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package core.parameters.parametertypes;

import java.text.NumberFormat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DoubleParameter extends AbstractParameter<Double> {

    private NumberFormat numberFormat = null;

    public DoubleParameter(String name,String description,
            String category,String source) {
        super(name, description, category, name,source);
    }

    public DoubleParameter(String name,String description,
                           String category,Double defaultValue,String source) {
        this(name, description, category, defaultValue,name,source);
    }

    public DoubleParameter(String name,String description,
            String category, Double defaultValue,String label,String source) {
        this(name, description, category,null,defaultValue,label, "");
    }

    public DoubleParameter(String name,  String description,
            String category,NumberFormat numberFormat,
            Double defaultValue,String codename,String source) {
        super(name, description, category, codename,source);
        setValue(defaultValue);
        this.numberFormat = numberFormat;
    }

    @Override
    public DoubleParameter clone() {
        DoubleParameter copy = new DoubleParameter(getName(), getDescription(),
                getCategory(), getNumberFormat(), getValue(), getLabel(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {

        try {
            setValue(Double.parseDouble(xmlElement.getTextContent()));
        }
        catch (NumberFormatException ex) {
        }


    }

    @SuppressWarnings("null")
    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue().toString());
    }

}
