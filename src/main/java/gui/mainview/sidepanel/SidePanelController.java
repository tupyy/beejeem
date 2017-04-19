package gui.mainview.sidepanel;

import core.CoreEvent;
import core.CoreEventType;
import core.CoreListener;
import core.job.Job;
import core.job.JobExecutionProgress;
import gui.ComponentEvent;
import gui.ComponentEventHandler;
import gui.DefaultComponentEvent;
import gui.MainController;
import gui.mainview.sidepanel.modules.ModulesController;
import gui.propertySheet.PropertyController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.JStesCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static main.JStesCore.getCoreEngine;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController implements Initializable, CoreListener,ComponentEventHandler{
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

        getCoreEngine().addCoreEventListener(this);
        JStesCore.registerController(this);

    }

    public void initialize(URL location, ResourceBundle resources) {

        assert parametersPane != null : "fx:id=\"parametersPane\" was not injected: check your FXML file 'parametersPane";

        propertyController = new PropertyController();
        parametersPane.setContent(propertyController.getPropertySheet());
        propertyController.getPropertySheet().prefWidthProperty().bind(parametersPane.widthProperty());
        componentControllerList.add(propertyController);

        //add module view
        addModuleView(vboxModulePanel);
    }

    @Override
    public void onComponentEvent(ComponentEvent event) {

        //Job selected
        if (event.getAction() == ComponentEvent.JOB_SELECTED) {
                UUID id = event.getJobId();
                logger.debug("Selected job id {}",id);
                Job j = getCoreEngine().getJob(id);
                currentJobID = j.getID();

                for (ComponentController componentController: componentControllerList) {
                    componentController.loadJob(j);
                }
            }
        else if(event.getAction() == ComponentEvent.JOB_DELETED) {
                if (currentJobID.equals(event.getJobId())) {
                    for (ComponentController componentController: componentControllerList) {
                        componentController.clear();
                    }
                    currentJobID = null;
                }
        }
    }


    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getId().equals(currentJobID)) {
            if (e.getAction() == CoreEventType.JOB_UPDATED) {
                for (ComponentController componentController: componentControllerList) {
                    componentController.updateJob(getCoreEngine().getJob(e.getId()));
                }
            }
        }
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    private void updateJob(Job job) {


    }

    /**
     * Show sidepanel view
     * @param parentNode
     */
    private void addModuleView(VBox parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("views/sidepanel/moduleView.fxml"));
            VBox command = (VBox) loader.load();

            ModulesController modulesController = loader.getController();
            componentControllerList.add(modulesController);
            parentNode.getChildren().add(command);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }



}
