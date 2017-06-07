package stes.isami.bjm.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.configuration.JobDefinition;
import stes.isami.bjm.configuration.Preferences;
import stes.isami.core.creator.Creator;
import stes.isami.core.creator.CreatorFactory;
import stes.isami.core.job.Job;
import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.parameters.parametertypes.StringParameter;
import stes.isami.core.util.XMLWorker;
import org.w3c.dom.Element;
import stes.isami.tcpserver.ClientMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class creates the jobs defined by the {@link ClientMessage}. The messages are sent
 * by the excel clients.
 * <p>A well defined message is as follows:</p>
 * <p><pre>
 *     <jobs>
 *         <job name="job name" creator="creator class">
 *              <parameter1>value</parameter1>
 *              <parameter2>value</parameter2>
 *          </job>
 *
 *          <job>
 *              ...
 *          </job>
 *     </jobs>

 * </pre></p>
 *
 */
public class TcpJobCreator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public TcpJobCreator() {

    }

    /**
     * Create the jobs from the element received from the client
     * @param payloadElement
     * @return the error messages
     */
    public List<Job> createJobs(Element payloadElement,List<String> errorMessages) {
        List<Job> createdJobs = new ArrayList<>();

        List<Element> jobElements = getJobElements(payloadElement);

        if (jobElements.size() == 0) {
            errorMessages.add("No <job> element found in the client message");
        }

        for (Element element: jobElements) {
            try {
                Creator creator = CreatorFactory.getCreator(getCreatorName(element));
                Job job = createJob(creator,getJobType(element),getFileFromElement(element),element);
                createdJobs.add(job);
            } catch (IOException e) {
                errorMessages.add("IOException " + e.getMessage());
                logger.error("IOException " + e.getMessage());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                errorMessages.add("Creator instantiation error for creator " +getCreatorName(element) + " exception: " + e.getMessage());
                logger.error(errorMessages.get(0));
            }
        }

        return createdJobs;
 }
    /**
     * Return a list with elements defined:
     * <pre>
     *     <job>
     *         ...
     *     </job>
     * </pre>
     * @param element
     * @return
     */
    private List<Element> getJobElements(Element element) {
        List<Element> jobList = new ArrayList<>();
        XMLWorker xmlWorker = new XMLWorker();
        jobList = xmlWorker.getElementsByName(element,"job");

        return jobList;
    }

    /**
     * Return the type of the job
     * <pre>
     *     <job name="job name" creator="creator class" type="job type">
     *         ...
     *     </job>
     * </pre>
     * @param element
     * @return
     */
    private String getJobType(Element element) {
        return element.getAttribute("type");
    }

    /**
     * Return the file defined in the:
     * <pre>
     *     <filename>filepath</filename>
     * </pre>
     * @param jobElement
     * @return
     */
    private File getFileFromElement(Element jobElement) {
        XMLWorker xmlWorker = new XMLWorker();
        Element fileElement = xmlWorker.getElementByName(jobElement,"filename");

        if (fileElement == null) {
            return null;
        }
        else {
            if ( !fileElement.getTextContent().isEmpty() ) {
                File file = new File(fileElement.getTextContent());
                return file;
            }
            else {
                return null;
            }
        }

    }
    /**
     * Get the creator class from {@link Element}
     * @param jobElement
     * @return
     */
    private String getCreatorName(Element jobElement) throws IllegalArgumentException {
        String creatorString = jobElement.getAttribute("creator");
        if (creatorString.isEmpty()) {
            throw new IllegalArgumentException("No creator attribute");
        }

        return creatorString;
    }

    private Job createJob(Creator creator, String jobType,File file, Element parameterValues) throws IOException,IllegalArgumentException {
        JobDefinition jobDefinition = JStesConfiguration.getPreferences().getJobDefinition(jobType);

        if (jobDefinition != null) {
            ParameterSet parameters = jobDefinition.getParameters();
            addParameterFromPreferences(parameters);

            List<Element> moduleElement = jobDefinition.getModuleElements();

            Job job = creator.createJob(Optional.ofNullable(file), parameterValues, parameters, moduleElement);
            return job;
        }
        else {
            throw new IllegalArgumentException("No job definition for creator: " + creator.getClass().getName());
        }

    }

    private ParameterSet addParameterFromPreferences(ParameterSet parameters) {
        Preferences preferences = JStesConfiguration.getPreferences();

        try {
            Parameter parameter = parameters.getParameter("localFolder");
            parameter.setValue(preferences.getValue("localFolder"));
        }
        catch (IllegalArgumentException ex) {
            StringParameter localFolder = new StringParameter("localFolder", "Local folder where all the result files are uploaded",
                    "Job", preferences.getValue("localFolder"), "Local folder", "external");
            parameters.addParameter(localFolder);
        }

        try {
            Parameter parameter = parameters.getParameter("destinationFolder");
            parameter.setValue(preferences.getValue("remoteFolder"));
        }
        catch (IllegalArgumentException ex) {
            StringParameter destinationFolder = new StringParameter("destinationFolder", "Remote folder",
                    "Job", preferences.getValue("remoteFolder"), "Remote folder", "external");
            parameters.addParameter(destinationFolder);
        }
        return parameters;
    }
}
