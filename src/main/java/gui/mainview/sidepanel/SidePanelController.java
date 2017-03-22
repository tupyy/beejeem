package gui.mainview.sidepanel;

import gui.propertySheet.PropertyController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane parametersPane;

    @FXML
    private TitledPane codePane;

    private SidePanelModel model = new SidePanelModel();

    private PropertyController propertyController;

    public void initialize(URL location, ResourceBundle resources) {

        assert parametersPane != null : "fx:id=\"parametersPane\" was not injected: check your FXML file 'parametersPane";
        assert codePane != null : "fx:id=\"codePane\" was not injected: check your FXML file 'codePane";

        propertyController = new PropertyController(model.getPropertyModel());
        parametersPane.getChildren().add(propertyController.getPropertySheet());
        propertyController.getPropertySheet().prefWidthProperty().bind(parametersPane.widthProperty());
    }
}
