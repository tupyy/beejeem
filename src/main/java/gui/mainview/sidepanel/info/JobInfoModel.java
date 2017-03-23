package gui.mainview.sidepanel.info;

import core.SimpleLogger;
import core.job.Job;
import core.job.JobExecutionProgress;
import core.job.JobState;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * Model class for the Information Pane of the SidePanelView
 */
public class JobInfoModel {
    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);

    private final JobInfoController controller;
    private SimpleStringProperty jobName = new SimpleStringProperty();
    private SimpleStringProperty jobID= new SimpleStringProperty();
    private SimpleStringProperty jobStatus= new SimpleStringProperty();
    private WebEngine webEngine;

    private SimpleLogger simpleLogger;
    private ListListener myListener = new ListListener();

    public JobInfoModel(JobInfoController jobInfoController) {
        this.controller = jobInfoController;
    }

    public void setJobLogger(JobExecutionProgress jobExecutionProgress) {

        if (simpleLogger != null) {
            simpleLogger.getInfoList().removeListener(myListener);
        }

        simpleLogger = (SimpleLogger) jobExecutionProgress;
        loadProgressData(simpleLogger);

        simpleLogger.getInfoList().addListener(myListener);
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


    private void loadProgressData(SimpleLogger simpleLogger) {
        controller.getLogArea().setText("");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s: simpleLogger.getInfoList()) {
            stringBuilder.append(s + "\n");
        }

        controller.getLogArea().setText(stringBuilder.toString());
    }

    private class  ListListener implements ListChangeListener<String> {
        @Override
        public void onChanged(Change<? extends String> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        String text = controller.getLogArea().getText();
                        text += simpleLogger.getInfoList().get(i) + "\n";
                        controller.getLogArea().setText(text);
                    }

                }
            }
        }
    }




}
