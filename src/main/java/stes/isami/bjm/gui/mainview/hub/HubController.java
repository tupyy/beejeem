package stes.isami.bjm.gui.mainview.hub;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;
import stes.isami.bjm.eventbus.*;
import stes.isami.bjm.gui.jobinfo.JobInfo;
import stes.isami.bjm.gui.mainview.hub.table.HubTableModel;
import stes.isami.bjm.main.JStesCore;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * CreatorController for the hubView
 */
public class HubController extends AbstractComponentEventHandler implements Initializable,JobListener {

    private static final Logger logger = LoggerFactory
            .getLogger(HubController.class);

    @FXML
    private TableView hubTable;

    @FXML
    private Button runJobButton;

    @FXML
    private Button runAllButton;

    @FXML
    private TextField filterField;

    private MyEventHandler myEventHandler;

    private boolean deleteInProgress = false;

    private HubTableModel model = new HubTableModel();
    private DeleteService deleteService;

    public HubController() {
        super();
        getCoreEngine().addJobListener(this);
    }

    public void initialize(URL location, ResourceBundle resources) {
        assert getHubTable() != null : "fx:id=\"hubTable\" was not injected: check your FXML file 'hubTable";
        assert runJobButton != null : "fx:id=\"runJobButton\" was not injected: check your FXML file 'hubTable";

        setupTable();

        setupActions();
        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");
    }

    @Override
    public void onCoreEvent(CoreEvent event) {
        if (event.getEventName() == CoreEvent.CoreEventType.SHUTDOWN) {
            model.shutdown();

            if (deleteService != null) {
                if (deleteService.isRunning()) {
                    deleteService.cancel();
                }
            }
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED) {
            runJobButton.setDisable(true);
            runAllButton.setDisable(true);
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_AUTHENTICATED) {
            if (getCoreEngine().count() > 0 ) {
                runAllButton.setDisable(false);
            }
        }
    }

    @Override
    public void onJobEvent(JobEvent action) {
        switch (action.getEvent()) {
            case DELETE:
                onDeleteAction(getHubTable().getSelectionModel().getSelectedItems());
        }
    }

    public TableView getHubTable() {
        return hubTable;
    }

    @Override
    public void jobUpdated(UUID id) {

        Job j = getCoreEngine().getJob(id);
        model.updateJob(j);

        disableRunAllButton();
        HubTableModel.JobData selection = (HubTableModel.JobData) getHubTable().getSelectionModel().getSelectedItem();
        if (selection != null) {
            if (selection.getId().equals(j.getID().toString())) {
                runJobButton.setDisable(false);
                setActionOnButton(runJobButton, getJobAction(JobState.toString(j.getState())).getActionType());
            }

        }
    }

    @Override
    public void jobCreated(UUID id) {
        model.addJob(getCoreEngine().getJob(id));
        runAllButton.setDisable(false);
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    private void setupTable() {

        getHubTable().getStylesheets().add(HubController.class.getClassLoader().getResource("css/hubTable.css").toExternalForm());
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
        FilteredList<HubTableModel.JobData> filteredData = new FilteredList<>(model.getData(), p -> true);

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


        statusCol.setCellFactory(column -> {
            return new TableCell<HubTableModel.JobData,String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);

                    TableRow<HubTableModel.JobData> currentRow = getTableRow();
                    if ( !isEmpty() ) {
                        if (item.equalsIgnoreCase("run")) {
                            currentRow.setId("row-run");
                        }
                        else if (item.equalsIgnoreCase("error")) {
                            currentRow.setId("row-error");
                        }
                        else if (item.equalsIgnoreCase("waiting")) {
                            currentRow.setId("row-waiting");
                        }
                        else if (item.equalsIgnoreCase("finished")) {
                            currentRow.setId("row-finished");
                        }
                        else {
                            currentRow.setId("");
                        }
                    }
                    else {
                         currentRow.setId("");
                    }

                }
            };
        });


        SortedList<HubTableModel.JobData> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(getHubTable().comparatorProperty());

        // Add sorted (and filtered) data to the table.
        getHubTable().setItems(sortedData);
        getHubTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        getHubTable().setRowFactory( tv -> {
            TableRow<HubTableModel.JobData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    HubTableModel.JobData rowData = row.getItem();
                    Job job = getCoreEngine().getJob(UUID.fromString(rowData.getId()));
                    if (job != null) {
                        showJobInfoDialog(job);
                    }
                }
            });
            return row ;
        });

    }

    private void showJobInfoDialog(Job job) {


       if (Files.isDirectory( (new File(job.getParameters().getParameter("temporaryFolder").getValue().toString())).toPath())) {

           Platform.runLater(() -> getStage().getScene().setCursor(Cursor.WAIT));

            Stage jobInfoDialog = new Stage();
            JobInfo jobInfo = new JobInfo(job);
            Pane root = jobInfo.getRootPane();

            if (root != null) {
                Scene scene = new Scene(jobInfo.getRootPane());

                jobInfoDialog.setScene(scene);
                jobInfoDialog.setTitle("Job ".concat(job.getName()));
                jobInfoDialog.setResizable(true);

                jobInfoDialog.initOwner(getStage());
                jobInfoDialog.initModality(Modality.APPLICATION_MODAL);
                jobInfoDialog.setWidth(1000);
                jobInfoDialog.setHeight(700);

                Platform.runLater(() -> getStage().getScene().setCursor(Cursor.DEFAULT));

                jobInfoDialog.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Job info");
            alert.setHeaderText("The temporary folder is not created. Please start the job and try again");
            alert.show();
        }
    }

    /**
     * Set up actions
     */
    private void setupActions() {
        getHubTable().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            if (!isDeleteInProgress()) {
                if (newSelection != null) {
                    HubTableModel.JobData jobData = (HubTableModel.JobData) newSelection;
                    onJobSelection(jobData);
                    }
            }
        });

        runAllButton.setOnAction((event) -> {
            getCoreEngine().executeAll();
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
                        jobState == JobState.ERROR
                        || jobState == JobState.FINISHED) {
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
    private void setActionOnButton(Button button, int action) {

        Platform.runLater(() -> {
                 if (action == MyEventHandler.RUN_ACTION) {
                    decorateButton(button, "images/start-icon.png");
                    button.setText("Run job");
                } else if (action == MyEventHandler.STOP_ACTION) {
                    decorateButton(button, "images/stop_red.png");
                    button.setText("Stop job");
                }

        });

    }

    private boolean isDeleteInProgress() {
        return this.deleteInProgress;
    }

    private void setDeleteInProgress(boolean deleteInProgress) {
        this.deleteInProgress = deleteInProgress;
    }

    /**
     * Perform the delete action
     * @param selectedJobs
     */
    private void onDeleteAction(ObservableList<HubTableModel.JobData> selectedJobs) {

        deleteService = new DeleteService();
        deleteService.setJobToDelete(new ArrayList<>(selectedJobs));
        deleteService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if ( (Boolean) event.getSource().getValue()) {
//                    JStesCore.getEventBus().post(new DefaultJobEvent(JobEvent.JobEventType.DESELECT));
                 }

                hubTable.requestFocus();
                setDeleteInProgress(false);

                HubTableModel.JobData jobData = (HubTableModel.JobData) hubTable.getSelectionModel().getSelectedItem();
                onJobSelection(jobData);

            }
        });

        setDeleteInProgress(true);
        deleteService.start();
    }

    /**
     * Perform action when a job has been selected in the hubTable
     * @param data
     */
    private void onJobSelection(HubTableModel.JobData data) {

        if (data != null) {
            if (runJobButton.isDisabled()) {
                runJobButton.setDisable(false);
            }
            setActionOnButton(runJobButton, getJobAction(data.getStatus()).getActionType());
            JStesCore.getEventBus().post(new DefaultJobEvent(HubController.this, JobEvent.JobEventType.SELECT, UUID.fromString(data.getId())));
        }
        else {
            JStesCore.getEventBus().post(new DefaultJobEvent(JobEvent.JobEventType.DESELECT));
        }
    }


    private Stage getStage() {
        return (Stage) getHubTable().getScene().getWindow();
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

            if (getActionType() == EMPTY_ACTION) {
                return;
            }

            ObservableList<HubTableModel.JobData> selection = getHubTable().getSelectionModel().getSelectedItems();

            if (selection.size() > -1) {
                for (HubTableModel.JobData jobData: selection) {
                    switch (getActionType()) {
                        case STOP_ACTION:
                            getCoreEngine().stopJob(UUID.fromString(jobData.getId()));
                            break;
                        case RUN_ACTION:
                            getCoreEngine().executeJob(UUID.fromString(jobData.getId()));
                            break;
                    }
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

    /**
     * Delete a list of jobs from source.
     * Return true if there is no job left in the coreEngine
     */
    private class DeleteService extends Service<Boolean> {

        private List<HubTableModel.JobData> list;

        public void setJobToDelete(List<HubTableModel.JobData> list) {
            this.list = list;
        }
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {

                    for (HubTableModel.JobData jobData : list) {
                        if (getCoreEngine().deleteJob(UUID.fromString(jobData.getId()))) {
                            model.deleteJob(jobData);
                        }
                    }

                    if (getCoreEngine().count() == 0) {
                        runAllButton.setDisable(true);
                        runJobButton.setDisable(true);

                       setActionOnButton(runJobButton,MyEventHandler.RUN_ACTION);
                        return true;
                    }

                    return false;
                }
            };
        }
    }

}
