package core.creator;

import core.job.Job;
import core.parameters.ParameterSet;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface to be implemented by all the class which creates jobs
 */
public interface Creator {

    /**
     * Create jobs.
     * If one of the {@code inputFiles} cannot be read, if processed silently to the next file.
     * <p>The list of elements having the values for the parameters can be defined as follows:</p>
     * <p><pre>
     *   <elements name="values for the first job">
     *       <parameter_name1>value</parameter_name1>
     *       <parameter_name2>value</parameter_name2>
     *   </elements>
     *   <elements name="values for the second job">
     *       <parameter_name1>value</parameter_name1>
     *       <parameter_name2>value</parameter_name2>
     *   </elements>
     *
     *   <p> The moduleElements are defined as follows
     * <pre>
     *     <modules>
     *         <module>
     *             <name>full class name of the module</name>
     *             <trigger>trigger name</trigger>
     *         </module>
     *     </modules>
     * </pre>
     * </p>
     * </pre>
     *
     * @param inputFiles external files used by Isami during the analysis (i.e. stf files)
     * @param parameterValues List of elements having the values for each parameter
     * @param parameterSet
     * @return
     */
    public List<Job> createJobs(Optional<List<File>> inputFiles, List<Element> parameterValues, ParameterSet parameterSet, List<Element> moduleElements) throws IllegalArgumentException,IOException;

    /**
     * Create jobs without external values for the job parameters
     * <p> The moduleElements are defined as follows
     * <pre>
     *     <modules>
     *         <module>
     *             <name>full class name of the module</name>
     *             <trigger>trigger name</trigger>
     *         </module>
     *     </modules>
     * </pre>
     * </p>
     * @param inputFiles
     * @param parameterSet
     * @return
     */
    public List<Job> createJobs(Optional<List<File>> inputFiles, ParameterSet parameterSet, List<Element> moduleElements) throws IllegalArgumentException,IOException;

}
