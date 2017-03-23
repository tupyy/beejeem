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

    public JobInfoModel(JobInfoController jobInfoController) {
        this.controller = jobInfoController;
    }

    public void setJobLogger(JobExecutionProgress jobExecutionProgress) {

        SimpleLogger logger = (SimpleLogger) jobExecutionProgress;

                logger.getInfoList().addListener((ListChangeListener.Change<? extends String> c) -> {
                            while (c.next()) {
                                if (c.wasAdded()) {
                                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                                       String text = controller.getLogArea().getText();
                                        text += logger.getInfoList().get(i) + "\n";
                                        controller.getLogArea().setText(text);
                                    }

                                }
                            }
                        }
                );
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



    private void addInfoMessage(String message) {

        Platform.runLater(() -> {

        });


    }

    private void clearDocument() {
        Document document = webEngine.getDocument();

        if (document != null) {
            Element el = document.getElementById("content");
            el.setTextContent("");
        }
    }

    public void startEngine(WebEngine webEngine) {
        this.webEngine = webEngine;
        webEngine.loadContent("<!DOCTYPE html><html><body><div id='content'></div></body></html>");
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Document doc = webEngine.getDocument();
            }
        });
    }

}
