package configuration;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 15/03/2017.
 */
public class JobDefinition {

    private String file;
    private ParameterSet parameters = new ParameterSet();
    private List<Element> moduleElements = new ArrayList<>();

    public JobDefinition() {

    }

    public JobDefinition(String file, ParameterSet parameters, List<Element> modules) {
        this.setParameters(parameters);
        this.setFile(file);
        this.setModuleElements(modules);
    }


    public String getName() {

        try {
            Parameter nameParameter = parameters.getParameter("name");
            return nameParameter.getValue().toString();
        }
        catch (IllegalArgumentException e) {
            return "";
        }
    }

    public String getType() {

        try {
            Parameter typeParameter = parameters.getParameter("type");
            return typeParameter.getValue().toString();
        }
        catch (IllegalArgumentException e) {
            return "";
        }
    }

    public String getCreator() {
        try {
            Parameter creatorParameter = parameters.getParameter("creator");
            return creatorParameter.getValue().toString();
        }
        catch (IllegalArgumentException e) {
            return "";
        }
    }
    public ParameterSet getParameters() {
        return parameters;
    }

    public List<Element> getModuleElements() {
        return moduleElements;
    }

    public void setParameters(ParameterSet parameters) {
        this.parameters = parameters;
    }

    public void setModuleElements(List<Element> moduleElements) {
        this.moduleElements = moduleElements;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
