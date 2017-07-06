package stes.isami.bjm.gui.mainview.sidepanel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;
import stes.isami.bjm.eventbus.AbstractComponentEventHandler;
import stes.isami.bjm.eventbus.JobEvent;
import stes.isami.bjm.gui.propertySheet.PropertyController;
import stes.isami.bjm.gui.propertySheet.PropertyEvent;
import stes.isami.bjm.gui.propertySheet.PropertyListener;
import stes.isami.bjm.main.JStesCore;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController extends AbstractComponentEventHandler implements Initializable,PropertyListener,JobListener {
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

        JStesCore.getCoreEngine().addJobListener(this);

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
                if (currentJobID != null) {
                    JStesCore.getCoreEngine().getJob(currentJobID).updateParametes(propertyController.getData());
                    applyButton.setDisable(true);
                    cancelButton.setDisable(true);
                }
            } catch (JobException e) {
                ;
            }
        });

        /**
         * Cancel editing
         * Update the job with the initial values
         */
        cancelButton.setOnAction(event -> {
            if (currentJobID != null) {
                propertyController.updateJob(JStesCore.getCoreEngine().getJob(currentJobID));
            }
        });

    }

    @Override
    public void jobUpdated(UUID id) {
        if (currentJobID == id) {
            for (ComponentController componentController: componentControllerList) {
                componentController.updateJob(JStesCore.getCoreEngine().getJob(id));
            }
        }
    }

    @Override
    public void jobCreated(UUID id) {
        for (ComponentController componentController: componentControllerList) {
            componentController.updateJob(JStesCore.getCoreEngine().getJob(id));
        }
    }

    @Override
    public void onStateChanged(UUID id, int newState) {

    }


    @Override
    public void onJobEvent(JobEvent event) {

        switch (event.getEvent()) {
            case SELECT:
                    UUID id = event.getJobId();
                    logger.debug("Selected job id {}", id);
                    Job j = JStesCore.getCoreEngine().getJob(id);
                    if (j != null ) {
                        currentJobID = j.getID();

                        for (ComponentController componentController : componentControllerList) {
                            componentController.loadJob(j);
                        }

                        applyButton.setDisable(true);
                        cancelButton.setDisable(true);
                    }
                    else {
                        clear();
                    }

                break;
            case DESELECT:
                clear();
                applyButton.setDisable(true);
                cancelButton.setDisable(true);
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

    private void clear() {
        for (ComponentController componentController: componentControllerList) {
            componentController.clear();
        }

    }

}
