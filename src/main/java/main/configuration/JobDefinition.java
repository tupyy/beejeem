package main.configuration;

import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 15/03/2017.
 */
public class JobDefinition {

    private ParameterSet parameters = new ParameterSet();
    private List<Element> moduleManagers = new ArrayList<>();

    public JobDefinition() {

    }

    public JobDefinition(ParameterSet parameters,List<Element> modules) {
        this.setParameters(parameters);
        this.setModuleManagers(modules);
    }


    public StringParameter getName() {

        try {
            StringParameter nameParameter = parameters.getParameter("name");
            return nameParameter;
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public StringParameter getType() {

        try {
            StringParameter nameParameter = parameters.getParameter("type");
            return nameParameter;
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ParameterSet getParameters() {
        return parameters;
    }

    public List<Element> getModuleManagers() {
        return moduleManagers;
    }

    public void setParameters(ParameterSet parameters) {
        this.parameters = parameters;
    }

    public void setModuleManagers(List<Element> moduleManagers) {
        this.moduleManagers = moduleManagers;
    }
}
