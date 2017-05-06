package gui.mainview.sidepanel;

import core.JobListener;
import core.job.Job;
import core.job.JobException;
import eventbus.*;
import gui.propertySheet.PropertyController;
import gui.propertySheet.PropertyEvent;
import gui.propertySheet.PropertyListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.JStesCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static main.JStesCore.getCoreEngine;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController extends AbstractComponentEventHandler implements Initializable,PropertyListener{
    private static final Logger logger = LoggerFactory
            .getLogger(SidePanelController.class);

    @FXML
    private VBox vboxContentPane;

    @FXML
    private ScrollPane parametersPane;

    @FXML
    private VBox vboxModulePanel;

    @FXML private Button applyButton;
    @FXML private Button cancelButton;


    private PropertyController propertyController;

    private List<ComponentController> componentControllerList = new ArrayList<>();
    private UUID currentJobID;

    public SidePanelController() {
        super();


    }

    public void initialize(URL location, ResourceBundle resources) {

        assert parametersPane != null : "fx:id=\"parametersPane\" was not injected: check your FXML file 'parametersPane";

        propertyController = new PropertyController();
        parametersPane.setContent(propertyController.getPropertySheet());
        propertyController.getPropertySheet().prefWidthProperty().bind(parametersPane.widthProperty());
        propertyController.registerListener(this);
        componentControllerList.add(propertyController);

        /**
         * Apply changes to the job
         */
        applyButton.setOnAction(event -> {
            try {
                JStesCore.getCoreEngine().getJob(currentJobID).updateParametes(propertyController.getData());
                applyButton.setDisable(true);
                cancelButton.setDisable(true);
            } catch (JobException e) {
                ;
            }
        });

        /**
         * Cancel editing
         * Update the job with the initial values
         */
        cancelButton.setOnAction(event -> {
            propertyController.updateJob(JStesCore.getCoreEngine().getJob(currentJobID));
        });

    }

    @Override
    public void onJobEvent(JobEvent event) {

        switch (event.getAction()) {
            case JOB_UPDATED:
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

                applyButton.setDisable(true);
                cancelButton.setDisable(true);
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
    public void parameterUpdated(PropertyEvent propertyEvent) {
        applyButton.setDisable(false);
        cancelButton.setDisable(false);
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    private void updateJob(Job job) {


    }



}
