package stes.isami.jstes.configuration;

import stes.isami.core.parameters.ParameterSet;
import javafx.beans.property.Property;

import java.util.List;

/**
 * Created by cosmin on 01/05/2017.
 */
public interface Preferences {

    public String getValue(String name);

    public void setValue(String name,String value);

    public Property getProperty(String name);

    public List<Property> getProperties();

    public List<String> getJobTypes();

    public JobDefinition getJobDefinition(String jobType);

    public void setJobDefinitions(List<JobDefinition> jobDefinitionList);

    public void addJobDefinition(JobDefinition jobDefinition);

    public List<JobDefinition> getJobDefinitions();


}
