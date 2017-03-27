package gui.mainview.sidepanel.info;

import core.job.Job;
import core.job.JobExecutionProgress;
import gui.mainview.sidepanel.ComponentController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tctupangiu on 22/03/2017.
 */

public class JobInfoController implements Initializable,ComponentController {
    private static final Logger logger = LoggerFactory
            .getLogger(JobInfoController.class);
    @FXML
    private Label nameLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea logArea;

    private JobInfoModel jobInfoModel;

    public JobInfoController() {
        this.jobInfoModel = new JobInfoModel(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        bindLabels(jobInfoModel);

    }

    @Override
    public void loadJob(Job job) {
        getModel().populate(job);
    }

    @Override
    public void updateJob(Job job) {
        getModel().populate(job);
    }

    @Override
    public void setJobProgressLogger(JobExecutionProgress jobProgressLogger) {
        getModel().setJobLogger(jobProgressLogger);
    }


    public JobInfoModel getModel() {
        return jobInfoModel;
    }

    /**
     * Bind text property for the label to the model
     *
     * @param model
     */
    private void bindLabels(JobInfoModel model) {
        nameLabel.textProperty().bind(model.getJobName());
        idLabel.textProperty().bind(model.getJobID());
        statusLabel.textProperty().bind(model.getJobStatus());
    }


    public TextArea getLogArea() {
        return logArea;
    }
}
