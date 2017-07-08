package stes.isami.bjm.components.sidepanel;

import stes.isami.core.job.Job;

/**
 * Created by tctupangiu on 23/03/2017.
 */
public interface ComponentController {

    /**
     * Load a job
     * @param job
     */
    public void loadJob(Job job);

    public void updateJob(Job job);

    /**
     * Clear models
     */
    public void clear();
}
