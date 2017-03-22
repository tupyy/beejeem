package gui.mainview.hub;

import core.CoreEvent;
import core.CoreEventType;
import core.CoreListener;
import core.job.JobListener;
import gui.mainview.hub.table.HubTableModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static core.JStesCore.getCoreEngine;

/**
 * CreatorController for the hubView
 */
public class HubController implements Initializable, CoreListener {

    @FXML
    private TableView hubTable;

    private HubTableModel tableModel = new HubTableModel();

    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        getCoreEngine().addCoreEventListener(this);
    }

    private void setupTable() {
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("name"));

        TableColumn destinationCol = new TableColumn("Destination");
        destinationCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("destinationFolder"));

        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("type"));

        TableColumn aircraftCol = new TableColumn("Aircraft");
        aircraftCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("aircraft"));

        TableColumn statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("status"));

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<HubTableModel.JobData,String>("id"));

        hubTable.getColumns().addAll(nameCol,destinationCol,typeCol,aircraftCol,statusCol,idCol);
        hubTable.setItems(tableModel.getData());
    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getAction() == CoreEventType.JOB_CREATED) {
            UUID id = e.getId();
            tableModel.addJob(getCoreEngine().getJob(id));
        }
    }
}
