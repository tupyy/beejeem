package core.creator;

import core.job.Job;
import core.parameters.ParameterSet;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface to be implemented by all the class which creates jobs
 */
public interface Creator {

    /**
     * Create a single job
     * @throws IllegalArgumentException if the parameters, code or modules element are not found in the jobDefinitionElement
     * @throws IOException if the spectre output file is not found
     */
    public Job createJob(File inputFile, Map<String, String> parameterValues, ParameterSet parameterSet,CreatorLog creatorLog) throws IllegalArgumentException, IOException;

    /**
     * Create jobs
     * @param inputFiles
     * @param parameterSet
     * @return
     */
    public List<Job> createJobs(List<File> inputFiles, ParameterSet parameterSet, List<Element> moduleElements,CreatorLog creatorLog) throws IOException;

    public String getJobType();
}
