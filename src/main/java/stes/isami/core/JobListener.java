package stes.isami.core;

import stes.isami.core.job.JobState;

import java.util.UUID;

/**
 * Interface for listen to the job events.
 * The job is sending events when the status has changed or a module has been finished running
 */
public interface JobListener {

    /**
     * Called when a job has been updated
     * @param id
     */
    void jobUpdated(UUID id);

    void jobCreated(UUID id);

    /**
     * The state changed
     * @param id
     */
    void onStateChanged(UUID id,int newState);
}
