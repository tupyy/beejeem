package core.garbage;

import com.sshtools.ssh.SshException;
import core.CoreEngine;
import core.modules.MethodResult;
import core.modules.ModuleException;
import core.modules.qdel.QDelModule;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import core.ssh.SshRemoteFactory;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class deletes from batch system all the jobs that have been deleted or stopped.
 */
public final class GarbageCollector {

    private final Logger logger = LoggerFactory.getLogger(GarbageCollector.class.getName());
    private final Thread thread;
    private final CoreEngine coreEngine;
    /**
     * List holding the batch ids to be deleted
     */
    BlockingDeque<JobEntry> jobQueue = new LinkedBlockingDeque<>();

    public GarbageCollector(CoreEngine coreEngine) {

        this.coreEngine = coreEngine;
        QDelModuleExecutor qDelModuleExecutor = new QDelModuleExecutor(this,jobQueue);
        thread = new Thread(qDelModuleExecutor);
        thread.start();
    }

    public void shutdown() {

        logger.debug("Shutting down the garbage collector");
        thread.interrupt();
    }

    /**
     * Get the output from qstat module and compare with the running job ids.
     * If a id is not in the running job ids, remove it from batch system
     * @param qStatOutput
     */
    public void setQStatOutput(MethodResult qStatOutput) {

        if (qStatOutput.getExitCode() == 0) {

            Thread newThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Parameter qstatOutputP = qStatOutput.getResultParameters().getParameter("qstatOutput");

                    List<String> batchIDList = parseQStatOutput(qstatOutputP.getValue().toString());
                    List<UUID> jobListID = coreEngine.getJobIDList();

                    for (String batchID: batchIDList) {
                        boolean batchFound = false;
                        for (UUID jobID: jobListID) {
                            try {
                                Parameter batchIDParameter = coreEngine.getJob(jobID).getParameter("batchID");
                                if (batchID.equals(batchIDParameter.getValue().toString())) {
                                    batchFound = true;
                                    break;
                                }
                            }
                            catch (IllegalArgumentException ex) {
                                ;
                            }
                        }

                        if (!batchFound) {
                            logger.debug("Job with batchID {} will be deleted",batchID);
                            registerJobForDeletion(UUID.randomUUID(),batchID);
                        }
                    }
                }
            });
            newThread.start();
        }
    }
    /**
     * Register a job for deletion.
     * @param batchID
     */
    public synchronized void registerJobForDeletion(UUID jobId, Parameter batchID) {

        logger.debug("Job {} with batch id {} registered for deletion",jobId,batchID.getValue().toString());
        jobQueue.add(new JobEntry(jobId,batchID));
    }

    public synchronized void registerJobForDeletion(UUID jobID,String batchID) {
        StringParameter batchIDParam = new StringParameter("batchID","","",batchID);
        registerJobForDeletion(jobID,batchIDParam);
    }

    public synchronized void jobDeletedSuccessfully(UUID jobID) {
        logger.debug("Job {} deleted successfully from batch system",jobID);
    }

    public synchronized void deletionError(UUID jobID,String errorMessage) {
        logger.debug("Error removing job {} from batch system: {}",jobID,errorMessage);
    }

    /**
     * Parse the qstat output to get the status of the job in the batch system
     * <br>If the batchID is not found in the output and it is set in the job, it means that the
     * job has been finished running in the batch system.
     * @param outString
     * @return the qstat status. If not found return empty string
     */
    private List<String> parseQStatOutput(String outString) {
        List<String> batchIDs = new ArrayList<>();

        final Pattern pattern = Pattern.compile("(\\d{6})");

        if (outString.isEmpty()) {
            return batchIDs;
        }

        Matcher m = pattern.matcher(outString);
        while (m.find()) {
            batchIDs.add(m.group());
        }

        return batchIDs;
    }

    public class JobEntry {

        private final Parameter batchId;
        private final UUID jobId;
        private int countTries = 2;

        public JobEntry(UUID jobId,Parameter batchId) {
            this.batchId = batchId;
            this.jobId = jobId;
        }


        /**
         * Get the qdel task
         * @return qdel task
         */
         public ModuleTask getTask() {
             QDelModule module = new QDelModule();
             try {
                 ModuleTask qdelTask = module.runModule(jobId, SshRemoteFactory.getSshClient(),getParameters());
                 return qdelTask;
             } catch (ModuleException | SshException e) {
                 return null;
             }
         }

         private ParameterSet getParameters() {
             ParameterSet parameters = new ParameterSet();
             parameters.addParameter(batchId);
             return parameters;
         }


        public UUID getID() {
            return jobId;
        }
    }
}
