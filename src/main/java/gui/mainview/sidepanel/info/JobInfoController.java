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

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public JobInfoModel getJobInfoModel() {
        return jobInfoModel;
    }

    public void setJobInfoModel(JobInfoModel jobInfoModel) {
        this.jobInfoModel = jobInfoModel;

        nameLabel.textProperty().bind(jobInfoModel.getJobName());
        idLabel.textProperty().bind(jobInfoModel.getJobID());
        statusLabel.textProperty().bind(jobInfoModel.getJobStatus());
    }
}
