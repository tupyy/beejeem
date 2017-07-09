package stes.isami.bjm.components.hub.presenter;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.components.hub.logic.JobData;
import stes.isami.bjm.components.hub.logic.ModelWorker;
import stes.isami.bjm.components.notifications.NotificationEvent;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobEvent;
import stes.isami.core.job.JobState;
import stes.isami.bjm.eventbus.*;
import stes.isami.bjm.components.jobinfo.JobInfo;
import stes.isami.bjm.main.JStesCore;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;
import static stes.isami.bjm.main.JStesCore.getEventBus;

/**
 * CreatorController for the hubView
 */
public class HubController extends AbstractComponentEventHandler implements JobListener {

    private static final Logger logger = LoggerFactory
            .getLogger(HubController.class);

    private final IHubView view;

    private final String HUB_TABLE_ID = "hubTable";
    private final String RUN_BUTTON_ID = "runJobButton";
    private final String RUN_ALL_BUTTON_ID="runAllButton";
    private final String DELETE_BUTTON_ID = "deleteButton";
    private TableView hubTable;
    /**
     * Service to update the job data in separate thread
     */
    private final ModelWorker modelWorker;
    private final Thread modelWorkderThread;

    private HubActionEventHandler myEventHandler;
    private SimpleIntegerProperty runButtonActionTypeProperty = new SimpleIntegerProperty(HubActionEventHandler.RUN_ACTION);

    private boolean deleteInProgress = false;

    /**
     * Disable property for buttons
     */
    SimpleBooleanProperty runJobButtonProperty = new SimpleBooleanProperty(true);
    SimpleBooleanProperty runAllJobProperty = new SimpleBooleanProperty(true);
    SimpleBooleanProperty disableDeleteProperty = new SimpleBooleanProperty(true);
    private EventHandler deleteActionHandler;

//    private DeleteService deleteService;

    public HubController(IHubView view) {
        super();
        getCoreEngine().addJobListener(this);
        getEventBus().register(this);

        this.view = view;

        //bind the disable property for the buttons
        try {
            view.getControl(RUN_BUTTON_ID).disableProperty().bind(runJobButtonProperty);
            view.getControl(RUN_ALL_BUTTON_ID).disableProperty().bind(runAllJobProperty);
            view.getControl(DELETE_BUTTON_ID).disableProperty().bind(disableDeleteProperty);
        }
        catch (NullPointerException e) {
            logger.error("Cannot find run buttons",e.getMessage());
        }

        //start the worker
        modelWorker = new ModelWorker(view.getData());
        modelWorkderThread= new Thread(modelWorker);
        modelWorkderThread.start();

        setupActions();
    }


    @Override
    public void onCoreEvent(CoreEvent event) {
        if (event.getEventName() == CoreEvent.CoreEventType.SHUTDOWN) {
            shutdownWorker();
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED) {
            runAllJobProperty.set(true);
            runJobButtonProperty.set(true);
            disableDeleteProperty.set(true);
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_AUTHENTICATED) {
            if (getCoreEngine().count() > 0 ) {
                runAllJobProperty.set(false);
            }
        }
    }


    @Override
    public void onJobEvent(JobEvent event) {
        switch (event.getEventType()) {
            case CREATE:
                jobCreated(event.getId());
                break;
            case UPDATE:
                jobUpdated(event.getId());
                break;
            case STATE_CHANGED:
                onStateChanged(event.getId());
                break;
            case START_DELETE:
                view.onStartDeletion();
                break;
            case END_DELETE:
                view.onEndDeletion();
                break;
            case DELETE:
                jobDeleted(event.getId());
                break;
        }
    }

    /**
     * Shutdown the thread of the modelWorker
     */
    public void shutdownWorker() {
        modelWorkderThread.interrupt();
    }


    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/



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
                    JobData jobData = (JobData) newSelection;
                    onJobSelection(jobData);
                }
            }
        });


        view.setActionEventHandler(RUN_ALL_BUTTON_ID, event -> {
            getCoreEngine().executeAll();
        });

        myEventHandler = new HubActionEventHandler(runButtonActionTypeProperty, (TableView) view.getControl(HUB_TABLE_ID));
        view.setActionEventHandler(RUN_BUTTON_ID,myEventHandler);

        //setup key events
        deleteActionHandler = new HubActionEventHandler(new SimpleIntegerProperty(HubActionEventHandler.DELETE_ACTION),(TableView) view.getControl(HUB_TABLE_ID));
        view.setActionEventHandler(DELETE_BUTTON_ID,deleteActionHandler);
        view.setKeyEventHandler(HUB_TABLE_ID, DELETE_BUTTON_ID,KeyCode.DELETE);
        view.setKeyEventHandler(HUB_TABLE_ID,RUN_BUTTON_ID,KeyCode.ENTER);

        /**
         * Set mouse event on the row. On double-click show the jobbInfoDialog
         */
        getHubTable().setRowFactory( tv -> {
            TableRow<JobData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    JobData rowData = row.getItem();
                    Job job = getCoreEngine().getJob(UUID.fromString(rowData.getId()));
                    if (job != null) {
                        showJobInfoDialog(job);
                    }
                }
            });
            return row;
        });

    }

    /**
     * Handler for CREATE job event
     * @param id
     */
    private void jobCreated(UUID id) {
        Job job  = getCoreEngine().getJob(id);
        view.getData().add(new JobData(job));
        runAllJobProperty.set(false);
    }

    /**
     * Handler for {@link stes.isami.core.job.JobEvent.JobEventType} state change
     * @param id
     */
    private void onStateChanged(UUID id) {
        Job j = getCoreEngine().getJob(id);
        modelWorker.onUpdateJob(j);

        //show notification
        if (j.getState() == JobState.FINISHED) {
            JStesCore.getEventBus().post(new NotificationEvent(NotificationEvent.NotiticationType.INFORMATION,
                    "Job " + j.getName(),
                    "Job \"" + j.getName() + "\" has finished"));
        }
        else if (j.getState() == JobState.ERROR) {
            JStesCore.getEventBus().post(new NotificationEvent(NotificationEvent.NotiticationType.ERROR,
                    "Job " + j.getName(),
                    "Job \"" + j.getName() + "\" has finished with error"));
        }
    }

    /**
     * Handler for {@link stes.isami.core.job.JobEvent.JobEventType} update
     * @param id
     */
    private void jobUpdated(UUID id) {

        Job j = getCoreEngine().getJob(id);
        modelWorker.onUpdateJob(j);

        disableRunAllButton();
        JobData selection = (JobData) getHubTable().getSelectionModel().getSelectedItem();
        if (selection != null) {
            if (selection.getId().equals(j.getID().toString())) {
                runJobButtonProperty.set(false);
                disableDeleteProperty.set(false);
                runButtonActionTypeProperty.set(getActionFromJobState(JobState.toString(j.getState())));
            }

        }
    }

    /**
     * Handler for {@link stes.isami.core.job.JobEvent.JobEventType} DELETE
     */
    private void jobDeleted(UUID id) {
        modelWorker.onDeleteJob(id);
    }


    /**
     * Return an action (i.e. run or stop) based on the state of the job
     * @param state
     * @return
     */
    private Integer getActionFromJobState(String state) {

        if (state.isEmpty()) {
            return HubActionEventHandler.EMPTY_ACTION;
        }
        else if (state.equals("Ready") || state.equals("Stop")
                || state.equals("Finished")
                || state.equals("Error")) {
              return HubActionEventHandler.RUN_ACTION;
        }

       return HubActionEventHandler.STOP_ACTION;
    }

    /**
     * Disable run all button
     */
    private void disableRunAllButton() {

        Platform.runLater(() -> {
            runAllJobProperty.set(true);
            for (UUID id: getCoreEngine().getJobIDList()) {
                int jobState = getCoreEngine().getJob(id).getState();
                if (jobState == JobState.READY ||
                        jobState == JobState.STOP ||
                        jobState == JobState.ERROR
                        || jobState == JobState.FINISHED) {
                    runAllJobProperty.set(false);
                    break;
                }
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
     * Perform action when a job has been selected in the hubTable
     * @param data
     */
    private void onJobSelection(JobData data) {

        if (data != null) {
            if (runJobButtonProperty.get()) {
                runJobButtonProperty.set(false);
            }
            runButtonActionTypeProperty.set(getActionFromJobState(data.getStatus()));
            disableDeleteProperty.set(false);
            JStesCore.getEventBus().post(new DefaultComponentEvent(HubController.this, ComponentEvent.JobEventType.SELECT, UUID.fromString(data.getId())));
        }
        else {
            JStesCore.getEventBus().post(new DefaultComponentEvent(ComponentEvent.JobEventType.DESELECT));
        }
    }


    private Stage getStage() {
        return (Stage) getHubTable().getScene().getWindow();
    }

    /**
     * Get the hub table
     * @return
     */
    private TableView getHubTable() {
        if (hubTable == null) {
            hubTable = (TableView) view.getControl(HUB_TABLE_ID);
        }
        return hubTable;
    }


}
