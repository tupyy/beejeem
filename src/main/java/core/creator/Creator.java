package core.creator;

import core.job.ModuleController;
import core.parameters.ParameterSet;

import java.io.IOException;
import java.util.List;

/**
 * Interface to be implemented by all the class which creates jobs
 */
public interface Creator {

    /**
     * Create jobs
     * @throws IllegalArgumentException if the parameters, code or modules element are not found in the jobDefinitionElement
     * @throws IOException if the spectre output file is not found
     */
    public void create() throws IllegalArgumentException, IOException;

    /**
     * Return the parameter sets created. For each job a parameter set is created
     * @return
     */
    public List<ParameterSet> getParameterSets();

    /**
     * Return a list of module manager for all jobs
     * @return
     */
    public List<ModuleController> getModules();
}
