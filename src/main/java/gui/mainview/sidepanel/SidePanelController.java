package gui.mainview.sidepanel;

import core.JobListener;
import core.job.Job;
import eventbus.ComponentAction;
import eventbus.CoreEvent;
import eventbus.JobEvent;
import eventbus.ComponentEventHandler;
import gui.propertySheet.PropertyController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.JStesCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static main.JStesCore.getCoreEngine;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController implements Initializable, ComponentEventHandler{
    private static final Logger logger = LoggerFactory
            .getLogger(SidePanelController.class);

    @FXML
    private VBox vboxContentPane;

    @FXML
    private ScrollPane parametersPane;

    @FXML
    private VBox vboxModulePanel;

    private PropertyController propertyController;

    private List<ComponentController> componentControllerList = new ArrayList<>();
    private UUID currentJobID;

    public SidePanelController() {

        JStesCore.registerController(this);

    }

    public void initialize(URL location, ResourceBundle resources) {

        assert parametersPane != null : "fx:id=\"parametersPane\" was not injected: check your FXML file 'parametersPane";

        propertyController = new PropertyController();
        parametersPane.setContent(propertyController.getPropertySheet());
        propertyController.getPropertySheet().prefWidthProperty().bind(parametersPane.widthProperty());
        componentControllerList.add(propertyController);

    }

    @Override
    public void onJobEvent(JobEvent event) {

        switch (event.getAction()) {
            case JOB_CREATED:
                for (ComponentController componentController: componentControllerList) {
                    componentController.updateJob(getCoreEngine().getJob(event.getJobId()));
                }
                break;
        }
    }

    @Override
    public void onComponentAction(ComponentAction event) {

        switch (event.getAction()) {
            case SELECT:
                UUID id = event.getJobId();
                logger.debug("Selected job id {}",id);
                Job j = getCoreEngine().getJob(id);
                currentJobID = j.getID();

                for (ComponentController componentController: componentControllerList) {
                    componentController.loadJob(j);
                }
                break;
            case DELETE:
                if (currentJobID.equals(event.getJobId())) {
                    for (ComponentController componentController: componentControllerList) {
                        componentController.clear();
                    }
                    currentJobID = null;
                }
                break;
        }
    }

    @Override
    public void onCoreEvent(CoreEvent event) {

    }


    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    private void updateJob(Job job) {


    }


}
