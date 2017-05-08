package gui.mainview.hub;

import core.job.Job;
import core.job.JobState;
import eventbus.*;
import gui.mainview.hub.table.HubTableModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.JStesCore;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static main.JStesCore.getCoreEngine;

/**
 * CreatorController for the hubView
 */
public class HubController extends AbstractComponentEventHandler implements Initializable {

    @FXML
    private TableView hubTable;

    @FXML
    private Button runJobButton;

    @FXML
    private Button runAllButton;

    @FXML
    private TextField filterField;

    private MyEventHandler myEventHandler;

    private HubModel model = new HubModel();

    public HubController() {
        super();
    }

    public void initialize(URL location, ResourceBundle resources) {
        assert getHubTable() != null : "fx:id=\"hubTable\" was not injected: check your FXML file 'hubTable";
        assert runJobButton != null : "fx:id=\"runJobButton\" was not injected: check your FXML file 'hubTable";

        setupTable();
        JStesCore.registerController(this);

        setupActions();
        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");
    }

    /**
     * {@inheritDoc}
     * @param event
     */
    @Override
    public void onJobEvent(JobEvent event) {

        switch (event.getAction()) {
            case JOB_DELETED:

                model.getTableModel().deleteJob(event.getJobId());

                Platform.runLater(() -> {
                    if (getCoreEngine().count() == 0) {
                        runAllButton.setDisable(true);
                        runJobButton.setDisable(true);
                    }
                });
                 break;
            case JOB_UPDATED:
                Job j = getCoreEngine().getJob(event.getJobId());
                model.getTableModel().updateJob(j);

                disableRunAllButton();
                HubTableModel.JobData selection = (HubTableModel.JobData) getHubTable().getSelectionModel().getSelectedItem();
                if (selection.getId().equals(j.getID().toString())) {
                    runJobButton.setDisable(false);
                    setActionOnButton(runJobButton,getJobAction(JobState.toString(j.getState())));
                }
                break;
            case JOB_CREATED:
                model.getTableModel().addJob(getCoreEngine().getJob(event.getJobId()));
                runAllButton.setDisable(false);
                runJobButton.setDisable(false);
        }
    }

    @Override
    public void onCoreEvent(CoreEvent event) {
        if (event.getEventName() == CoreEvent.CoreEventType.SHUTDOWN) {
            model.getTableModel().shutdown();
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
                // If filter text is empty, display all rows.
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
                if (jobData != null) {
                    setActionOnButton(runJobButton,getJobAction(jobData.getStatus()));
                    JStesCore.getEventBus().post(new DefaultComponentAction(HubController.this, ComponentAction.ComponentActions.SELECT, UUID.fromString(jobData.getId())));
                }
            }
        });

        runAllButton.setOnAction((event) -> {
            JStesCore.getEventBus().post(new DefaultComponentAction(HubController.this,ComponentAction.ComponentActions.EXECUTE_ALL,UUID.randomUUID()));
            runAllButton.setDisable(true);
        });

        myEventHandler = new MyEventHandler(MyEventHandler.RUN_ACTION);
        runJobButton.setOnAction(myEventHandler);

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
     * Return an action (i.e. run or stop) based on the state of the job
     * @param state
     * @return
     */
    private MyEventHandler getJobAction(String state) {

        if (state.isEmpty()) {
            myEventHandler.setActionType(MyEventHandler.EMPTY_ACTION);
        }
        else if (state.equals("Ready") || state.equals("Stop")
                || state.equals("Finished")
                || state.equals("Error")) {
               myEventHandler.setActionType(MyEventHandler.RUN_ACTION);
                return myEventHandler;
        }

        myEventHandler.setActionType(MyEventHandler.STOP_ACTION);

        return myEventHandler;

    }

    private void disableRunAllButton() {

        Platform.runLater(() -> {
            runAllButton.setDisable(true);
            for (UUID id: getCoreEngine().getJobIDList()) {
                int jobState = getCoreEngine().getJob(id).getState();
                if (jobState == JobState.READY ||
                        jobState == JobState.STOP ||
                        jobState == JobState.ERROR) {
                    runAllButton.setDisable(false);
                    break;
                }
            }
        });
    }
    /**
     * Set the action handler on the button
     * @param button
     * @param action
     */
    private void setActionOnButton(Button button, MyEventHandler action) {

        Platform.runLater(() -> {
            if (action != null) {
                if (action.getActionType() == MyEventHandler.RUN_ACTION) {
                    decorateButton(button, "images/start-icon.png");
                    button.setText("Run job");
                } else if (action.getActionType() == MyEventHandler.STOP_ACTION) {
                    decorateButton(button, "images/stop_red.png");
                    button.setText("Stop job");
                }
            }
        });

    }

    /**
     * Implantation for the EventHandler to have the name of the action
     *
     */
    private class MyEventHandler implements EventHandler<ActionEvent> {

        public static final int STOP_ACTION = 1;
        public static final int RUN_ACTION = 2;
        public static final int EMPTY_ACTION = 3;

        private int actionType;

        public MyEventHandler(int actionType) {
            this.setActionType(actionType);
        }

        @Override
        public void handle(ActionEvent event) {

            ComponentAction.ComponentActions actionType = ComponentAction.ComponentActions.STOP;

            if (getActionType() == EMPTY_ACTION) {
                return;
            }

            if (getActionType() == STOP_ACTION) {
                actionType = ComponentAction.ComponentActions.STOP;
            }
            else if (getActionType() == RUN_ACTION) {
                actionType = ComponentAction.ComponentActions.EXECUTE;
            }

            ObservableList<HubTableModel.JobData> selection = getHubTable().getSelectionModel().getSelectedItems();

            if (selection.size() > -1) {
                for (HubTableModel.JobData jobData: selection) {
                    JStesCore.getEventBus().post(new DefaultComponentAction(HubController.this,actionType,UUID.fromString(jobData.getId())));
                }
            }
        }

        public int getActionType() {
            return actionType;
        }

        public void setActionType(int actionType) {
            this.actionType = actionType;
        }
    }

}
