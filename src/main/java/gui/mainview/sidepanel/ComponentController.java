package gui.mainview.sidepanel;

import core.job.Job;
import core.job.JobExecutionProgress;

/**
 * Created by tctupangiu on 23/03/2017.
 */
public interface ComponentController {

    public void loadJob(Job job);

    public void updateJob(Job job);

    public void setJobProgressLogger(JobExecutionProgress jobProgressLogger);

}