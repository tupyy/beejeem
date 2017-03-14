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

package core.parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import core.parameters.parametertypes.BooleanParameter;
import core.parameters.parametertypes.DoubleParameter;
import core.parameters.parametertypes.StringParameter;
import org.w3c.dom.Element;

/**
 * Simple storage for the parameters.
 */
public class ParameterSet  implements Iterable<Parameter<?>>,Cloneable {

 //   private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private UUID ID = java.util.UUID.randomUUID();

    private final List<Parameter<?>> parameters = new ArrayList<>();

    public ParameterSet(Parameter<?>... items) {
        for (Parameter<?> p : items) {
            parameters.add(p);
        }
    }

    /**
     * Represent methods's parameters and their values in human-readable format
     */
    public String toString() {

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            Parameter<?> param = parameters.get(i);
            Object value = param.getValue();
            s.append(param.getName());
            s.append(": ");
            s.append(String.valueOf(value));
            if (i < parameters.size() - 1)
                s.append(", ");
        }
        return s.toString();
    }

    /**
     * Make a copy
     */
    public ParameterSet clone() {
        ParameterSet newSet = new ParameterSet();

        for (Parameter<?> param : parameters) {
            Parameter<?> newParam  = param.clone();
            newSet.addParameter(newParam);
        }
        return newSet;
    }

    public List<Parameter<?>> getParameters() {
        return parameters;
    }

    /**
     *  Get a parameter based on the ID
     * @param parameterID
     * @param <T>
     * @return
     */
    public <T extends Parameter<?>> T getParameter(UUID parameterID) {
        for (Parameter<?> p : parameters) {
            if (p.getID().equals(parameterID))
                return (T) p;
        }
        throw new IllegalArgumentException(
                "Parameter with ID " + parameterID + " does not exist");
    }

    /**
     *  Get a parameter
     * @param parameterName name of the parameter
     * @return T parameter
     */
    public <T extends Parameter<?>> T getParameter(String parameterName) {
        for (Parameter<?> p : parameters) {
            if (p.getName().equals(parameterName))
                return (T) p;
        }
        throw new IllegalArgumentException(
                "Parameter with ID" + parameterName + " does not exist");
    }

    /**
     * Add parameter
     * @param parameter
     */
    public void addParameter(Parameter<?> parameter) {
        parameters.add(parameter);
    }

    @Override
    public Iterator<Parameter<?>> iterator() {
        return parameters.iterator();
    }

    public UUID getID() {
        return ID;
    }

    /**
     * Return true if every parameter has a value
     */
    public boolean isValid() {
        for (Parameter p: parameters) {
            if ( !p.isValid() ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Load parameters definition from xml elements
     * @param parameterElements list of xml elements which define the parameters
     */
    public void loadDefinitionFromXML(ArrayList<Element> parameterElements) {

        for (Element parameterElement: parameterElements) {
            String sourceType = parameterElement.getAttribute("source");
            if (sourceType.isEmpty()) {
                sourceType = "internal";
            }

            String paramType = parameterElement.getAttribute("type");
            Parameter<?> parameter = null;
            if (paramType.equalsIgnoreCase("string")) {
                parameter = new StringParameter(parameterElement.getAttribute("name"), null, null, null, null,sourceType);
                parameter.loadValueFromXML(parameterElement);
                this.addParameter(parameter);
            } else if (paramType.equalsIgnoreCase("boolean")) {
                parameter = new BooleanParameter(parameterElement.getAttribute("name"), null, null,sourceType);
                parameter.loadValueFromXML(parameterElement);
                this.addParameter(parameter);
            } else if (paramType.equalsIgnoreCase("double")) {
                parameter = new DoubleParameter(parameterElement.getAttribute("name"), null, null,sourceType);
                parameter.loadValueFromXML(parameterElement);
                this.addParameter(parameter);
            }

        }
    }

    /**
     * Load the parameters values from list.
     * Each value in the list is attributed to a parameter having the same index as the value.
     * @param list
     */
    public void loadValueFromExternal(ArrayList<String> list) {

        int i= 0;
        for (Parameter<?> p: parameters) {
            if (i == list.size()) break;
            if (p.getSource().equalsIgnoreCase("external")) {
                if (p instanceof BooleanParameter) {
                    BooleanParameter bp = (BooleanParameter) p;
                    bp.setValue(list.get(i));
                }
                else if (p instanceof StringParameter) {
                    StringParameter sp = (StringParameter) p;
                    sp.setValue(list.get(i));
                }
                else if (p instanceof DoubleParameter) {
                    DoubleParameter dp = (DoubleParameter) p;
                    dp.setValue(list.get(i));
                }
                i++;
            }
        }
    }


    public void saveValuesToXML(Element xmlElement) {
//        Document parentDocument = xmlElement.getOwnerDocument();
//        for (Parameter<?> param : parameters) {
//            Element paramElement = parentDocument
//                    .createElement(parameterElement);
//            paramElement.setAttribute(nameAttribute, param.getName());
//            xmlElement.appendChild(paramElement);
//            param.saveValueToXML(paramElement);
//        }
    }


}
