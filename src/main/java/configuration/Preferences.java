package configuration;

import core.parameters.ParameterSet;
import javafx.beans.property.Property;

import java.util.List;

/**
 * Created by cosmin on 01/05/2017.
 */
public interface Preferences {

    public String getValue(String name);

    public Property getProperty(String name);

    public List<String> getJobTypes();

    /**
     * Return the parameters of a job.
     * @param jobType
     * @return empty ParameterSet if not found
     */
    public ParameterSet getJobParameterSet(String jobType);

}
