package stes.isami.core;

import stes.isami.core.job.JobEvent;

/**
 * Interface for listen to the job events.
 * The job is sending events when the status has changed or a module has been finished running
 */
public interface JobListener {

   void onJobEvent(JobEvent event);
}
