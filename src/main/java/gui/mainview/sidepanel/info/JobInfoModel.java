package gui.mainview.sidepanel.info;

import core.job.Job;
import core.job.JobExecutionProgress;
import core.job.JobState;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 * Model class for the Information Pane of the SidePanelView
 */
public class JobInfoModel {

    private final JobInfoController controller;
    private SimpleStringProperty jobName = new SimpleStringProperty();
    private SimpleStringProperty jobID= new SimpleStringProperty();
    private SimpleStringProperty jobStatus= new SimpleStringProperty();

    public JobInfoModel(JobInfoController jobInfoController) {
        this.controller = jobInfoController;
    }

    public void populate(Job job, JobExecutionProgress jobExecutionProgress) {
        populate(job);
    }

    public void populate(Job job) {

        Platform.runLater(() -> {
            jobName.set(job.getName());
            jobID.set(job.getID().toString());
            jobStatus.set(JobState.toString(job.getStatus()));
        });

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

    public JobInfoController getController() {
        return controller;
    }
}
