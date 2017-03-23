package gui.mainview.hub;

import core.CoreEvent;
import core.CoreEventType;
import core.CoreListener;
import core.job.Job;
import core.job.JobListener;
import gui.MainController;
import gui.mainview.hub.table.HubTableModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.UUID;

import static core.JStesCore.getCoreEngine;

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
    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getAction() == CoreEventType.JOB_CREATED) {
            UUID id = e.getId();
            model.getTableModel().addJob(getCoreEngine().getJob(id));
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

        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("type"));
        typeCol.setMaxWidth(400);
        typeCol.setMinWidth(100);

        TableColumn aircraftCol = new TableColumn("Aircraft");
        aircraftCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("aircraft"));
        aircraftCol.setMaxWidth(400);
        aircraftCol.setMinWidth(100);

        TableColumn statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("status"));
        statusCol.setMaxWidth(400);
        statusCol.setMinWidth(100);

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("id"));

        hubTable.getColumns().addAll(nameCol,destinationCol,typeCol,aircraftCol,statusCol,idCol);
        hubTable.setItems(model.getTableModel().getData());
        hubTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }




}
