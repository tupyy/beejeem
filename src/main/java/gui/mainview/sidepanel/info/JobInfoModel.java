package gui.mainview.sidepanel.info;

import core.job.Job;
import core.job.JobExecutionProgress;
import core.job.JobState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 * Model class for the Information Pane of the SidePanelView
 */
public class JobInfoModel {

    private SimpleStringProperty jobName = new SimpleStringProperty();
    private SimpleStringProperty jobID= new SimpleStringProperty();
    private SimpleStringProperty jobStatus= new SimpleStringProperty();

    public JobInfoModel() {

    }

    public void populate(Job job, JobExecutionProgress jobExecutionProgress) {
        jobName.set(job.getName());
        jobID.set(job.getID().toString());
        jobStatus.set(JobState.toString(job.getStatus()));
    }

    public ObservableStringValue getJobName() {
        return jobName;
    }

    public ObservableStringValue getJobID() {
        return jobID;
    }

    public ObservableStringValue getJobStatus() {
        return jobStatus;
    }
}