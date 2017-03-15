package main.configuration;

import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 14/03/2017.
 */
public final class JStesPreferences  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Holds the definition of jobs
     */
    private List<JobDefinition> jobs = new ArrayList<>();

    private ParameterSet userConfiguration = new ParameterSet();

    public JStesPreferences() {

    }

    //<editor-fold desc="User definition SECTION">
    /**
     * User configuration
     */
    public ParameterSet getUserConfiguration() {
        return userConfiguration;
    }

    public void setUserConfiguration(ParameterSet userConfiguration) {
        this.userConfiguration = userConfiguration;
    }

    /**
     * Return the value of the {@code name} parameter
     * @return return the value or empty string if not defined
     */
    public String getUserConfValue(String key) {
        try {
            return (String) getUserConfiguration().getParameter(key).getValue();
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

    /**
     * Set a new value to the element {@code name}
     * @param name
     * @param value
     */
    public void setUserConfValue(String name, String value) {
        try {
            StringParameter element = getUserConfiguration().getParameter(name);
            element.setValue(value);
        }
        catch (IllegalArgumentException ex) {
            ;
        }
    }
    //</editor-fold>

    /**
     * Get the job types
     * @return list of job types
     */
    public List<String> getJobTypes() {

        List<String> retJobTypes = new ArrayList<>();
        for (JobDefinition jd: jobs) {
            StringParameter jobType = jd.getType();
            if (jobType != null) {
                retJobTypes.add(jobType.getLabel());
            }
        }

        return retJobTypes;
    }

    public void addJobDefition(JobDefinition jobDefinition) {
        jobs.add(jobDefinition);
    }

    /**
     * Get a list of job configurations of type {@code jobType}
     * @param name of the job
     * @return the {@link JobDefinition} defining the job or null if not found
     */
    public JobDefinition getJob(String name) {

        for (JobDefinition jd: jobs) {
            if (jd.getName().equals(name)) {
                return jd;
            }
        }

        return null;
    }


}
