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
     * The values for the parameters are read from the {@param xmlFile}. The structure of the file is:
     * <p><pre>
     *   <job name="jobName">
     *       <parameter_name1>value</parameter_name1>
     *       <parameter_name2>value</parameter_name2>
     *   </job>
     * </pre>
     * </p>
     * </pre>
     *
     * If the {@param inputFiles} is present, the name of the file associated with a set of values must be present in the
     * element set which defines the job
     * <p><pre>
     *     <job name="job name">
     *       <file>name of the file in {@param inputFiles}</file>
     *       <parameter_name1>value</parameter_name1>
     *       <parameter_name2>value</parameter_name2>
     *   </job>
     * </pre></p>
     *
     *
     * @param parameterValues element containing the parameters value
      * @param parameterSet
     * @return
     */
    public Job createJob(Optional<File> inputFile,Element parameterValues, ParameterSet parameterSet, List<Element> moduleElements) throws IllegalArgumentException,IOException;

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
