package configuration;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import javafx.beans.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 14/03/2017.
 */
public final class JStesPreferences  implements Preferences{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Holds the definition of jobs
     */
    private List<JobDefinition> jobs = new ArrayList<>();

    private List<Property> configurationSet = new ArrayList<>();

    public JStesPreferences() {

    }

    public List<Property> getConfiguration() {
        return configurationSet;
    }

    public void setConfigurationSet(List configuration) {
        this.configurationSet = configuration;
    }


    //<editor-fold desc="Preference ">
    /**
     * User configuration
     */

    /**
     * Return the value of the {@code name} parameter
     * @return return the value or empty string if not defined
     */
    @Override
    public String getValue(String name) {
        Property property = getProperty(name);
        if (property != null) {
            return (String) property.getValue();
        }

        return "";
    }

    /**
     * Set a new value to the element {@code name}
     * @param name
     * @param value
     */
    @Override
    public void setValue(String name, String value) {
        Property property = getProperty(name);
        if (property != null) {
            property.setValue(value);
        }
    }

    @Override
    public Property getProperty(String name) {

        for (Property property: configurationSet) {
            if (property.getName().equals(name)) {
                return property;
            }
        }

        return null;
    }

    @Override
    public List<Property> getProperties() {
        return configurationSet;
    }


    /**
     * Get the job types
     * @return list of job types
     */
    @Override
    public List<String> getJobTypes() {

        List<String> retJobTypes = new ArrayList<>();
        for (JobDefinition jd: jobs) {
            String jobType = jd.getType();
            if ( !jobType.isEmpty() ) {
                retJobTypes.add(jobType);
            }
        }

        return retJobTypes;
    }

    /**
     * Get a list of job configurations of type {@code jobType}
     * @param jobType of the job
     * @return the {@link JobDefinition} defining the job or null if not found
     */
    @Override
    public JobDefinition getJobDefinition(String jobType) {

        for (JobDefinition jd: jobs) {
            if (jd.getType().equals(jobType)) {
                return jd;
            }
        }

        return null;
    }

    @Override
    public void setJobDefinitions(List<JobDefinition> jobDefinitionList) {
        this.jobs = jobDefinitionList;
    }

    @Override
    public void addJobDefinition(JobDefinition jobDefinition) {
        jobs.add(jobDefinition);
    }

    @Override
    public List<JobDefinition> getJobDefinitions() {
        return jobs;
    }

    //</editor-fold>



}
