package gui.mainview.hub;

import core.CoreEvent;
import core.CoreEventType;
import core.CoreListener;
import core.job.Job;
import gui.MainController;
import gui.mainview.hub.table.HubTableModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static main.JStesCore.getCoreEngine;

/**
 * CreatorController for the hubView
 */
public class HubController implements Initializable, CoreListener {

    @FXML
    private TableView hubTable;

    @FXML
    private Button runJobButton;

    @FXML
    private Button runAllButton;

    private MainController mainController;

    private HubModel model = new HubModel();

    public void initialize(URL location, ResourceBundle resources) {
        assert hubTable != null : "fx:id=\"hubTable\" was not injected: check your FXML file 'hubTable";
        assert runJobButton != null : "fx:id=\"runJobButton\" was not injected: check your FXML file 'hubTable";

        setupTable();
        getCoreEngine().addCoreEventListener(this);

        setupActions();
        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");
    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getAction() == CoreEventType.JOB_CREATED) {
            UUID id = e.getId();
            model.getTableModel().addJob(getCoreEngine().getJob(id));
            runJobButton.setDisable(false);
            runAllButton.setDisable(false);
        }
        else if (e.getAction() == CoreEventType.JOB_UPDATED) {
            Job j = getCoreEngine().getJob(e.getId());
            model.getTableModel().updateJob(j);
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    private void setupTable() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("name"));

        TableColumn destinationCol = new TableColumn("Destination");
        destinationCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("destinationFolder"));

        TableColumn localFolderCol = new TableColumn("Local folder");
        localFolderCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("localFolder"));

        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("type"));
        typeCol.setMaxWidth(100);
        typeCol.setMinWidth(100);
        typeCol.setPrefWidth(100);
        typeCol.setResizable(false);

        TableColumn statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("status"));
        statusCol.setMaxWidth(100);
        statusCol.setMinWidth(100);
        statusCol.setPrefWidth(100);
        statusCol.setResizable(false);

        TableColumn batchIDCol = new TableColumn("Batch ID");
        batchIDCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("batchID"));
        batchIDCol.setMaxWidth(100);
        batchIDCol.setMinWidth(100);
        batchIDCol.setPrefWidth(100);
        batchIDCol.setResizable(false);


        TableColumn aircraftCol = new TableColumn("Aircraft");
        aircraftCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("aircraft"));
        aircraftCol.setVisible(false);

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("id"));
        idCol.setVisible(false);

        hubTable.getColumns().addAll(nameCol,localFolderCol,destinationCol,typeCol,statusCol,batchIDCol,aircraftCol,idCol);
        hubTable.setItems(model.getTableModel().getData());
        hubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Set up actions
     */
    private void setupActions() {
        hubTable.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection) -> {
            if (newSelection != null) {
                HubTableModel.JobData selectedData = (HubTableModel.JobData) newSelection;
                mainController.getSidePanelController().onJobSelected(selectedData.getId(),model.getJobLogger(UUID.fromString(selectedData.getId())));
            }
        });

        runJobButton.setOnAction((event) -> {
            ObservableList<HubTableModel.JobData> selection = hubTable.getSelectionModel().getSelectedItems();

            if (selection.size() > -1) {
                for (HubTableModel.JobData jobData: selection) {
                    getCoreEngine().executeJob(UUID.fromString(jobData.getId()), model.getJobLogger(UUID.fromString(jobData.getId())));
                }
            }
        });

        runAllButton.setOnAction((event) -> {
            for (HubTableModel.JobData jobData: model.getTableModel().getData()){
                getCoreEngine().executeJob(UUID.fromString(jobData.getId()), model.getJobLogger(UUID.fromString(jobData.getId())));
            }
        });
    }

    /**
     * Add icons to buttons
     */
    private void decorateButton(Button button,String imagePath) {
        URL s = HubController.class.getClassLoader().getResource(imagePath);
        ImageView imageView = new ImageView(new Image(s.toString()));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        button.setGraphic(imageView);
    }


}
