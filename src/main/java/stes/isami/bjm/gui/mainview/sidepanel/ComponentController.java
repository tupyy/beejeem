package stes.isami.bjm.gui.mainview.sidepanel;

import stes.isami.core.job.Job;

import java.util.UUID;

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

    public void setDisableJob(boolean state);

    /**
     * Clear models
     */
    public void clear();
}
