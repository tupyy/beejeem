package stes.isami.bjm.components.hub.presenter;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.components.hub.logic.JobData;
import stes.isami.bjm.components.jobinfo.JobInfo;
import stes.isami.bjm.components.notifications.NotificationEvent;
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

    @FXML private TableView hubTable;

    @FXML private Button runJobButton;

    @FXML private Button runAllButton;
    @FXML private Button deleteButton;

    @FXML private TextField filterField;

    @FXML private StackPane mainPane;

    private SimpleIntegerProperty buttonActionType;

    private HubController controller;
    private ObservableList<JobData> modelData;
    private MaskerPane maskerPane = new MaskerPane();

    private static final int RUN_ACTION = 200;
    private static final int STOP_ACTION = 100;

    public HubViewImpl() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        decorateButton(runJobButton,"images/start-icon.png");
        decorateButton(runAllButton,"images/start-icon.png");
        decorateButton(deleteButton,"images/remove.png");
        deleteButton.setTooltip(new Tooltip("Delete selected jobs"));
        setupTableListeners();

        maskerPane.setVisible(false);
        mainPane.getChildren().add(maskerPane);

    }

    @Override
    public void update(Observable o, Object arg) {
        JobData jobData = (JobData) o;
        sendNotification(jobData);
    }


    @Override
    public void onStartDeletion() {
        Platform.runLater(() -> {
            maskerPane.setVisible(true);
        });
    }

    @Override
    public void onEndDeletion() {
        Platform.runLater(() -> {
            maskerPane.setVisible(false);
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
        setDataToTable(hubTable);

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
                runAllButton.setDisable(false);
            }
        });
    }

    @Override
    public List<UUID> getSelectedJobs() {
        List<UUID> ids = new ArrayList<>();

        hubTable.getSelectionModel().getSelectedItems().forEach(item -> {
            ids.add(UUID.fromString(((JobData) item).getId()));
        });

        return ids;
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

    private void setDataToTable(TableView tableView) {
        //Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList filteredData = new FilteredList<>(modelData,p-> true);

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

    private void setupTableListeners() {


        hubTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                 deleteButton.setDisable(false);
                 runJobButton.setDisable(false);
                 controller.onJobSelection(UUID.fromString(((JobData) newValue).getId()));

                 int action = STOP_ACTION;
                 for(Object obj: hubTable.getSelectionModel().getSelectedItems()) {
                     action = getActionFromJobState(((JobData) obj).getStatus());
                 }
                changeRunButtonDecoration(action);

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

    private void changeRunButtonDecoration(int action) {
        switch (action) {
            case STOP_ACTION:
                decorateButton(runJobButton,"images/stop_red.png");
                runJobButton.setText("Stop job");
                break;
            case RUN_ACTION:
                decorateButton(runJobButton,"images/start-icon.png");
                runJobButton.setText("Run job");
                break;
        }
    }

    /**
     * Disable all buttons
     * @param disableAllButton
     */
    private void setDisableAllButton(boolean disableAllButton) {
        runJobButton.setDisable(disableAllButton);
        runAllButton.setDisable(disableAllButton);
        deleteButton.setDisable(disableAllButton);
    }

    /**
     * Disable runallButton
     * If there is no job to be executed, the button will disable
     */
    private void changeRunAllState() {

        Platform.runLater(() -> {
            runAllButton.setDisable(true);
            for (JobData jobData: modelData) {
                String jobState = jobData.getStatus();
                if (jobState.equals("Ready") ||
                        jobState.equals("Stop")||
                        jobState.equals("Error")
                        || jobState.equals("Finished")) {
                    runAllButton.setDisable(false);
                    break;
                }
            }
        });
    }

    /**
     * Return an action (i.e. run or stop) based on the state of the job
     * @param state
     * @return
     */
    private Integer getActionFromJobState(String state) {

        if (state.equals("Ready") || state.equals("Stop")
                || state.equals("Finished")
                || state.equals("Error")) {
            return RUN_ACTION;
        }

        return STOP_ACTION;
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
