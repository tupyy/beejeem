package gui.mainview.hub;

import core.CoreEvent;
import core.CoreEventType;
import core.CoreListener;
import core.job.Job;
import core.job.JobState;
import gui.ComponentEvent;
import gui.ComponentEventHandler;
import gui.DefaultComponentEvent;
import gui.MainController;
import gui.mainview.hub.table.HubTableModel;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.JStesCore;

import java.net.URL;
import java.util.*;
import java.util.stream.Collector;

import static main.JStesCore.getCoreEngine;

/**
 * CreatorController for the hubView
 */
public class HubController implements Initializable, CoreListener, ComponentEventHandler {

    @FXML
    private TableView hubTable;

    @FXML
    private Button runJobButton;

    @FXML
    private Button runAllButton;

    @FXML
    private TextField filterField;

    private MainController mainController;

    private HubModel model = new HubModel();

    public void initialize(URL location, ResourceBundle resources) {
        assert getHubTable() != null : "fx:id=\"hubTable\" was not injected: check your FXML file 'hubTable";
        assert runJobButton != null : "fx:id=\"runJobButton\" was not injected: check your FXML file 'hubTable";

        setupTable();
        getCoreEngine().addCoreEventListener(this);
        JStesCore.registerController(this);

        setupActions();
        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");


    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getAction() == CoreEventType.JOB_CREATED) {
            UUID id = e.getId();
            model.getTableModel().addJob(getCoreEngine().getJob(id));
            runAllButton.setDisable(false);
        }
        else if (e.getAction() == CoreEventType.JOB_UPDATED) {
            Job j = getCoreEngine().getJob(e.getId());
            model.getTableModel().updateJob(j);
        }
        else if (e.getAction() == CoreEventType.JOB_DELETED) {
            model.getTableModel().deleteJob(e.getId());
            JStesCore.getEventBus().post(new DefaultComponentEvent(this,ComponentEvent.JOB_DELETED, e.getId()));

            if (getCoreEngine().count() == 0) {
                runAllButton.setDisable(true);
                runJobButton.setDisable(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param event
     */
    @Override
    public void onComponentEvent(ComponentEvent event) {

        switch (event.getAction()) {
            case ComponentEvent.JOB_SELECTED:

                if (isJobIdle(event.getJobId())) {
                    runJobButton.setDisable(false);
                }
                else {
                    runJobButton.setDisable(true);
                }
                 break;

            case ComponentEvent.JOB_DELETED:
                runJobButton.setDisable(true);
                runAllButton.setDisable(true);
                 break;

            case ComponentEvent.SELECTION_CLEARED:
                runJobButton.setDisable(true);
                break;
        }
    }

    public TableView getHubTable() {
        return hubTable;
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

        getHubTable().getColumns().addAll(nameCol,localFolderCol,destinationCol,typeCol,statusCol,batchIDCol,aircraftCol,idCol);
        getHubTable().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<HubTableModel.JobData> filteredData = new FilteredList<>(model.getTableModel().getData(), p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(jobData -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (jobData.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (jobData.getDestinationFolder().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                else if (jobData.getLocalFolder().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (jobData.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (jobData.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (jobData.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });

            getHubTable().getSelectionModel().clearSelection();
        });

        SortedList<HubTableModel.JobData> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(getHubTable().comparatorProperty());

        // Add sorted (and filtered) data to the table.
        getHubTable().setItems(sortedData);
        getHubTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



    }

    /**
     * Set up actions
     */
    private void setupActions() {
        getHubTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            if (newSelection != null) {
                HubTableModel.JobData jobData = (HubTableModel.JobData) newSelection;
                UUID id = UUID.fromString(jobData.getId());
                JStesCore.getEventBus().post(new DefaultComponentEvent(this,ComponentEvent.JOB_SELECTED,id));
            }

        });

        runJobButton.setOnAction((event) -> {
            ObservableList<HubTableModel.JobData> selection = getHubTable().getSelectionModel().getSelectedItems();

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

//        stopButton.setOnAction(event -> {
//            ObservableList<HubTableModel.JobData> selection = getHubTable().getSelectionModel().getSelectedItems();
//
//            if (selection.size() > -1) {
//                for (HubTableModel.JobData jobData: selection) {
//                    getCoreEngine().stopJob(UUID.fromString(jobData.getId()));
//                }
//            }
//        });
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

    /**
     * Check if a job can be executed (i.e. the state is IDLE)
     * @param id
     * @return
     */
    private boolean isJobIdle(UUID id) {
        if (getCoreEngine().getJob(id).getStatus() == JobState.IDLE) {
            return true;
        }

        return false;
    }


}
