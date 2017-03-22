package gui.mainview.sidepanel;

import core.job.Job;
import gui.MainController;
import gui.propertySheet.PropertyController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static core.JStesCore.getCoreEngine;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController implements Initializable{
    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);

    @FXML
    private VBox vboxContentPane;

    @FXML
    private AnchorPane parametersPane;

    @FXML
    private TitledPane codePane;

    private SidePanelModel model = new SidePanelModel();

    private PropertyController propertyController;
    private MainController mainController;

    public void initialize(URL location, ResourceBundle resources) {

        assert parametersPane != null : "fx:id=\"parametersPane\" was not injected: check your FXML file 'parametersPane";
        assert codePane != null : "fx:id=\"codePane\" was not injected: check your FXML file 'codePane";

        propertyController = new PropertyController(model.getPropertyModel());
        parametersPane.getChildren().add(propertyController.getPropertySheet());
        propertyController.getPropertySheet().prefWidthProperty().bind(parametersPane.widthProperty());
    }

    public void onJobSelected(String id) {

        logger.info("Selected job id {}",id);
        Job j = getCoreEngine().getJob(UUID.fromString(id));
        model.loadJobParameters(j.getParameters());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public MainController getMainController() {
        return mainController;
    }
}
