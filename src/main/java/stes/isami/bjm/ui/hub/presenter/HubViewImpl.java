package stes.isami.bjm.ui.hub.presenter;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.ui.hub.logic.JobData;
import stes.isami.bjm.ui.jobinfo.JobInfo;
import stes.isami.bjm.ui.notifications.NotificationEvent;
import stes.isami.bjm.main.JStesCore;
import stes.isami.core.job.Job;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Hub view class
 */
public class HubViewImpl implements Initializable,HubView,Observer {
    private static final Logger logger = LoggerFactory
            .getLogger(HubViewImpl.class);

    private final JobStateTask jobStateTask;
    private final Thread jobStateThread;

    @FXML private TableView hubTable;
    @FXML private Button runJobButton;
    @FXML private Button stopButton;
    @FXML private Button runAllButton;
    @FXML private Button deleteButton;
    @FXML private TextField filterField;
    @FXML private StackPane mainPane;

    private SimpleBooleanProperty disableRunButton = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty disableRunAllButton = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty disableStopButton = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty disableDeleteButton = new SimpleBooleanProperty(true);

    private HubController controller;
    private ObservableList<JobData> modelData;
    private MaskerPane maskerPane = new MaskerPane();
    private boolean suspendSelection;

    public HubViewImpl() {
        jobStateTask = new JobStateTask(disableRunButton, disableStopButton);
        jobStateTask.setSelectionList(new ArrayList<>());
        jobStateThread = new Thread(jobStateTask);
        jobStateThread.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");
        decorateButton(deleteButton,"images/remove.png");
        deleteButton.setTooltip(new Tooltip("Delete selected jobs"));
        decorateButton(stopButton,"images/stop_red.png");
        stopButton.setTooltip(new Tooltip("Stop selected jobs"));

        createTableListeners();
        createButtonActions();

        runJobButton.disableProperty().bind(disableRunButton);
        runAllButton.disableProperty().bind(disableRunAllButton);
        stopButton.disableProperty().bind(disableStopButton);
        deleteButton.disableProperty().bind(disableDeleteButton);

        maskerPane.setVisible(false);
        mainPane.getChildren().add(maskerPane);
    }

    @Override
    public void update(Observable o, Object arg) {
        JobData jobData = (JobData) o;
        sendNotification(jobData);

        if (hubTable.getSelectionModel().getSelectedItems().indexOf(jobData) >= 0) {
            disableRunStopButton();
        }
    }


    @Override
    public void onStartDeletion() {
        onSuspendSelectionAction(true);
//        maskerPane.setVisible(true);
    }

    @Override
    public void onEndDeletion() {
        onSuspendSelectionAction(false);
        Platform.runLater(() -> {
//            maskerPane.setVisible(false);
            hubTable.requestFocus();
        });


    }

    @Override
    public void setController(HubController controller) {
        this.controller = controller;
        setData(controller.getData());
    }

    @Override
    public void onSshDisconnect() {
        setDisableAllButton(true);
    }

    @Override
    public void onSshAuthenticated() {

    }

    @Override
    public void setData(ObservableList<JobData> data) {
        this.modelData = data;
        setupTable(hubTable);
        setFilter(hubTable,modelData);

        /**
         * Add listener to model.
         */
        modelData.addListener((ListChangeListener<JobData>) c -> {

            if (modelData.size() == 0) {
                setDisableAllButton(true);
            }
            else {
                while (c.next()) {
                    for (JobData additem : c.getAddedSubList()) {
                        additem.addObserver(this);
                    }
                }
                disableRunAllButton.set(false);
                disableDeleteButton.set(false);
            }
        });
    }

    @Override
    public List<UUID> getSelectedJobs() {
        List<UUID> ids = new ArrayList<>();

        if (!isSelectionSuspended()) {
            hubTable.getSelectionModel().getSelectedItems().forEach(item -> {
                ids.add(UUID.fromString(((JobData) item).getId()));
            });
        }

        return ids;
    }


    /**
     * Handle the shutdown event from app
     */
    public void shutdown() {
        jobStateThread.interrupt();
    }


    /************************************************
     *
     *
     *                     PRIVATE
     *
     *
     *************************************************/

    private void setupTable(TableView tableView) {

        tableView.getStylesheets().add(HubViewImpl.class.getClassLoader().getResource("css/hubTable.css").toExternalForm());
        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("name"));

        TableColumn destinationCol = new TableColumn("Destination");
        destinationCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("destinationFolder"));

        TableColumn localFolderCol = new TableColumn("Local folder");
        localFolderCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("localFolder"));

        TableColumn typeCol = new TableColumn("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("type"));
        typeCol.setMaxWidth(150);
        typeCol.setMinWidth(150);
        typeCol.setPrefWidth(150);
        typeCol.setResizable(false);

        TableColumn statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("status"));
        statusCol.setMaxWidth(100);
        statusCol.setMinWidth(100);
        statusCol.setPrefWidth(100);
        statusCol.setResizable(false);

        TableColumn batchIDCol = new TableColumn("Batch ID");
        batchIDCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("batchID"));
        batchIDCol.setMaxWidth(100);
        batchIDCol.setMinWidth(100);
        batchIDCol.setPrefWidth(100);
        batchIDCol.setResizable(false);

        TableColumn idCol = new TableColumn("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<JobData,String>("id"));
        idCol.setVisible(false);

        tableView.getColumns().addAll(nameCol,localFolderCol,destinationCol,typeCol,statusCol,batchIDCol,idCol);
        tableView.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }

    /**
     * Create and set the filter for the table data
     * @param tableView
     */
    private void setFilter(TableView tableView,ObservableList<JobData> data) {
        //Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList filteredData = new FilteredList<>(data,p-> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(jobData -> {

                JobData jobData1 = (JobData) jobData;
                // If filter text is empty, display all rows.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (jobData1.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (jobData1.getDestinationFolder().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (jobData1.getLocalFolder().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (jobData1.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (jobData1.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (jobData1.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });

            tableView.getSelectionModel().clearSelection();
        });

        SortedList<JobData> sortedData = new SortedList<>(filteredData);

        //Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }

    /**
     * Set the table listeners
     */
    private void createTableListeners() {

         hubTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !isSelectionSuspended()) {
                controller.onJobSelection(UUID.fromString(((JobData) newValue).getId()));
                disableRunStopButton();
            }
        });

         hubTable.setOnKeyPressed((KeyEvent e) -> {
             if (e.getCode() == KeyCode.DELETE) {
                 controller.onActionPerformed(DELETE_ACTION);
             }
             else if (e.getCode() == KeyCode.ENTER) {
                 controller.onActionPerformed(RUN_JOB_ACTION);
             }
         });



        /**
         * Set mouse event on the row. On double-click show the jobbInfoDialog
         */
        hubTable.setRowFactory( tv -> {
            TableRow<JobData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    JobData rowData = row.getItem();
                    Job job = getCoreEngine().getJob(UUID.fromString(rowData.getId()));
                    if (job != null) {
                        showJobInfoDialog(job); //TODO change job to id
                    }
                }
            });
            return row;
        });
    }

    /**
     * Add icons to buttons
     */
    private void decorateButton(Button button,String imagePath) {
        URL s = HubControllerImpl.class.getClassLoader().getResource(imagePath);
        ImageView imageView = new ImageView(new Image(s.toString()));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        button.setGraphic(imageView);
    }

    /**
     * Disable all buttons
     * @param value
     */
    private void setDisableAllButton(boolean value) {
       disableDeleteButton.set(true);
       disableRunAllButton.set(true);
       disableRunButton.set(true);
       disableStopButton.set(true);
    }
    /**
     * Create the action handlers for the buttons
     */
    private void createButtonActions() {
        runJobButton.setOnAction(event -> {
            controller.onActionPerformed(HubView.RUN_JOB_ACTION);
        });

        runAllButton.setOnAction(event -> {
            controller.onActionPerformed(HubView.RUN_ALL_ACTION);
        });

        stopButton.setOnAction(event -> {
            controller.onActionPerformed(HubView.STOP_ACTION);
        });

        deleteButton.setOnAction(event -> {
            controller.onActionPerformed(HubView.DELETE_ACTION);
        });
    }

    /**
     *  set value to {@code suspendSelection}
     * @param suspendSelection
     */
    private void onSuspendSelectionAction(boolean suspendSelection) {
        this.suspendSelection = suspendSelection;
    }

    /**
     * Return true if the selection action is suspended
     * @return
     */
    private boolean isSelectionSuspended() {
        return suspendSelection;
    }

    /**
     * Get the list of selected jobs and feed it to the {@link JobStateTask} which will
     * disable/enable the runJobButton and stopJobButton accordingly
     */
    private void disableRunStopButton() {
        jobStateTask.setSelectionList(hubTable.getSelectionModel().getSelectedItems());
    }
    /**
     * Send notification when a job is finished
     * @param jobData
     */
    private void sendNotification(JobData jobData) {
        if (jobData.getStatus().equals("Finished")) {
            JStesCore.getEventBus().post(new NotificationEvent(NotificationEvent.NotiticationType.INFORMATION,
                    "Job " + jobData.getName(),
                    "Job \"" + jobData.getName() + "\" has finished"));
        } else if (jobData.getStatus().equals("Error")) {
            JStesCore.getEventBus().post(new NotificationEvent(NotificationEvent.NotiticationType.ERROR,
                    "Job " + jobData.getName(),
                    "Job \"" + jobData.getName() + "\" has finished with error"));
        }
    }
    /**
     * Return the stage
     * @return
     */
    private Stage getStage() {
        return (Stage) hubTable.getScene().getWindow();
    }

    /**
     * Show the jobInfoDialog
     * @param job
     */
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

}
