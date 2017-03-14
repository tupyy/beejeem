package core.job;

import java.util.UUID;

/**
 * Interface for listen to the job events.
 * The job is sending events when the status has changed or a module has been finished running
 */
public interface JobListener {

    /**
     * Job added
     * @param jobID
     */
    public void jobAdded(UUID jobID);

    /**
     * Job deleted
     * @param jobID id of the deleted job
     */
    public void jobDeleted(UUID jobID);

    /**
     * Job changed status
     * @param jobID the id of the job
     */
    public void statusChanged(UUID jobID);
}
