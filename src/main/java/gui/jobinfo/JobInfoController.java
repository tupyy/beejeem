package gui.jobinfo;

import core.job.JobException;
import core.parameters.Parameter;
import gui.MainController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Controller for code and batch pane
 */
public class JobInfoController implements Initializable{

    public static final int CODE_FILE = 1;
    public static final int BATCH_FILE = 2;
    public static final int HTML_FILE = 3;

    @FXML private Button applyCodeButton;
    @FXML private Button cancelCodeButton;
    @FXML private Button closeWindowButton;
    @FXML private TextArea codeTextArea;
    @FXML private TextArea batchFileTextArea;
    @FXML private WebView resultWebArea;

    private JobInfo parent;
    private String initialCode;
    private ChangeListener<String> codeAreaListener;


    public void setParent(JobInfo parent) {
        this.parent = parent;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButtonActions();

        applyCodeButton.setDisable(true);
        cancelCodeButton.setDisable(true);

         codeAreaListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                applyCodeButton.setDisable(false);
                cancelCodeButton.setDisable(false);
            }
        };

    }


    /**
     * Consume the content of the file {@code fileType}
     * @param fileType
     * @param s
     */
    public void accept(int fileType, String s) {

        Platform.runLater(() -> {
            switch (fileType) {
                case CODE_FILE:
                    codeTextArea.replaceText(0,0,s);
                    initialCode = s;
                   codeTextArea.textProperty().addListener(codeAreaListener);
                    break;
                case BATCH_FILE:
                    batchFileTextArea.clear();
                    batchFileTextArea.replaceText(0,0,s);
                    break;
                case HTML_FILE:
                    resultWebArea.getEngine().loadContent(s);
                    break;
            }
        });

    }

    public void setJobEditable(boolean editable) {
        codeTextArea.setEditable(editable);
    }

    /**
     * Create button actions
     */
    private void createButtonActions() {

        applyCodeButton.setOnAction(event -> {

            Alert replaceCodeAlert = new Alert(Alert.AlertType.CONFIRMATION);
            replaceCodeAlert.setHeaderText("Edit job code");
            replaceCodeAlert.setContentText("You are about to change permanently the code of this job. \n Are you sure about this ?");

            Optional<ButtonType> result = replaceCodeAlert.showAndWait();
            if (result.get() == ButtonType.OK){
                initialCode = codeTextArea.getText();
                Parameter codeParameter = parent.getJob().getParameters().getParameter("pythonCode");
                codeParameter.setValue(codeTextArea.getText());
                try {
                    parent.getJob().updateParameter(codeParameter);
                } catch (JobException e) {

                }
            }
            else {
                resetCodeJobArea();
            }


        });

        cancelCodeButton.setOnAction(event -> {
            resetCodeJobArea();
        });

        closeWindowButton.setOnAction(event -> {

            if (parent != null) {
                parent.close();
            }

            // close the dialog.
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        });
    }

    private void resetCodeJobArea() {
        Platform.runLater(() -> {
            codeTextArea.textProperty().removeListener(codeAreaListener);
            codeTextArea.clear();
            codeTextArea.setText(initialCode);
            applyCodeButton.setDisable(true);
            cancelCodeButton.setDisable(true);
            codeTextArea.textProperty().addListener(codeAreaListener);
        });
    }

}
