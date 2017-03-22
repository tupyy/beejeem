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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
