package gui.jobinfo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
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


    public void setParent(JobInfo parent) {
        this.parent = parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButtonActions();
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
                    break;
                case BATCH_FILE:
                    batchFileTextArea.replaceText(0,0,s);
                    break;
                case HTML_FILE:
                    resultWebArea.getEngine().loadContent(s);
                    break;
            }
        });

    }

    /**
     * Create button actions
     */
    private void createButtonActions() {

        applyCodeButton.setOnAction(event -> {

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


}
