package gui.mainview.sidepanel;

import core.CoreEvent;
import core.CoreEventType;
import core.CoreListener;
import core.job.Job;
import core.job.JobExecutionProgress;
import gui.MainController;
import gui.mainview.sidepanel.info.JobInfoController;
import gui.mainview.sidepanel.modules.ModulesController;
import gui.propertySheet.PropertyController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static core.JStesCore.getCoreEngine;

/**
 * CreatorController class for the CommandView
 */
public class SidePanelController implements Initializable, CoreListener{
    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);

    @FXML
    private VBox vboxContentPane;

    @FXML
    private AnchorPane parametersPane;

    @FXML
    private VBox vboxModulePanel;

    @FXML
    private VBox vboxInfoPanel;

    @FXML
    private TitledPane codePane;

    private SidePanelModel sidePanelModel;

    private PropertyController propertyController;
    private MainController mainController;
    private ModulesController modulesController;
    private JobInfoController jobInfoController;
    private UUID currentJobID;

    public SidePanelController() {

        sidePanelModel = new SidePanelModel(this);
        getCoreEngine().addCoreEventListener(this);

    }

    public void initialize(URL location, ResourceBundle resources) {

        assert parametersPane != null : "fx:id=\"parametersPane\" was not injected: check your FXML file 'parametersPane";
        assert codePane != null : "fx:id=\"codePane\" was not injected: check your FXML file 'codePane";

        propertyController = new PropertyController(sidePanelModel.getPropertyModel());
        parametersPane.getChildren().add(propertyController.getPropertySheet());
        propertyController.getPropertySheet().prefWidthProperty().bind(parametersPane.widthProperty());

        //add module view
        addModuleView(vboxModulePanel);
        sidePanelModel.setModulesModel(modulesController.getModel());

        //add info view
        addInfoView(vboxInfoPanel);
        sidePanelModel.setJobInfoModel(jobInfoController.getModel());

    }

    /**
     * Perform action when a job has been selected in the hubView
     * @param id
     */
    public void onJobSelected(String id, JobExecutionProgress jobExecutionProgress) {

        logger.info("Selected job id {}",id);
        Job j = getCoreEngine().getJob(UUID.fromString(id));
        
        sidePanelModel.onJobSelected(j,jobExecutionProgress);
        setEditable(j.isEditable());
    }

    /**
     * Set the main controller
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Get the main controller
     * @return
     */
    public MainController getMainController() {
        return mainController;
    }


    /**
     * Disable the editing if the job has become not editable
     * @param editable
     */
    public void setEditable(boolean editable) {
        modulesController.setEditable(editable);
        propertyController.setEditable(editable);
    }

    public UUID getCurrentJobID() {
        return currentJobID;
    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getId().equals(currentJobID)) {
            if (e.getAction() == CoreEventType.JOB_UPDATED) {
                updateJob(getCoreEngine().getJob(e.getId()));
            }
        }
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    private void updateJob(Job job) {
        setEditable(job.isEditable());

        //just update the model without the JobExecutionProgress
        sidePanelModel.onJobSelected(job,null);
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

            modulesController = loader.getController();
            parentNode.getChildren().add(command);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Show sidepanel view
     * @param parentNode
     */
    private void addInfoView(VBox parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("views/sidepanel/infoView.fxml"));
            VBox command = (VBox) loader.load();

            jobInfoController = loader.getController();
            parentNode.getChildren().add(command);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }



}
