package gui.mainview.sidepanel;

import javafx.beans.value.ObservableNumberValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController implements Initializable{

    @FXML
    private VBox vboxContentPane;

    @FXML
    private TitledPane parametersPane;

    @FXML
    private TitledPane codePane;

    public void initialize(URL location, ResourceBundle resources) {

        Parent parentNode =  vboxContentPane.getParent();
//        vboxContentPane.maxWidthProperty().bind(parentNode.widthProperty().multiply(0.3));
    }
}
