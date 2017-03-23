package gui.mainview.sidepanel.info;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tctupangiu on 22/03/2017.
 */

public class JobInfoController implements Initializable{

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

    public void setModel(JobInfoModel jobInfoModel) {
        this.jobInfoModel = jobInfoModel;
        bindLabels(jobInfoModel);
    }

    public JobInfoModel getModel() {
        return jobInfoModel;
    }

    /**
     * Bind text property for the label to the model
     * @param model
     */
    private void bindLabels(JobInfoModel model) {
        nameLabel.textProperty().bind(model.getJobName());
        idLabel.textProperty().bind(model.getJobID());
        statusLabel.textProperty().bind(model.getJobStatus());
    }
}
