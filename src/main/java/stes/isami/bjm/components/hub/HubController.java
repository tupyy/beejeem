package stes.isami.bjm.components.hub;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.components.hub.table.HubActionEventHandler;
import stes.isami.bjm.components.hub.table.JobData;
import stes.isami.bjm.components.hub.table.ModelWorker;
import stes.isami.bjm.components.notifications.NotificationEvent;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;
import stes.isami.bjm.eventbus.*;
import stes.isami.bjm.components.jobinfo.JobInfo;
import stes.isami.bjm.components.hub.table.HubTableModel;
import stes.isami.bjm.main.JStesCore;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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

//    private DeleteService deleteService;

    public HubController(IHubView view) {
        super();
        getCoreEngine().addJobListener(this);
        getEventBus().register(this);

        this.view = view;

        //bind the disable property for the buttons
        try {
            Button b = (Button) view.getControl("runJobButton");
            b.disableProperty().bind(runJobButtonProperty);

            view.getControl("runAllButton").disableProperty().bind(runAllJobProperty);
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

//            if (deleteService != null) {
//                if (deleteService.isRunning()) {
//                    deleteService.cancel();
//                }
//            }
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED) {
            runAllJobProperty.set(true);
            runJobButtonProperty.set(true);
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_AUTHENTICATED) {
            if (getCoreEngine().count() > 0 ) {
                runAllJobProperty.set(false);
            }
        }
    }

    @Override
    public void onComponentEvent(ComponentEvent componentEvent) {
        switch (componentEvent.getEvent()) {
            case DELETE:
                onDeleteAction(getHubTable().getSelectionModel().getSelectedItems());
        }
    }

    /**
     * Patch to be able to delete job from others classes
     * @param jobID
     */
    @Subscribe
    public void onDeleteJobHack(UUID jobID) {
//        JobData jobData = model.getJobData(jobID);
//        if (jobData != null) {
//            onDeleteAction(FXCollections.observableArrayList(jobData));
//        }
    }

    @Override
    public void jobUpdated(UUID id) {

        Job j = getCoreEngine().getJob(id);
        modelWorker.onUpdateJob(j);

        disableRunAllButton();
        JobData selection = (JobData) getHubTable().getSelectionModel().getSelectedItem();
        if (selection != null) {
            if (selection.getId().equals(j.getID().toString())) {
                runJobButtonProperty.set(false);
                runButtonActionTypeProperty.set(getActionFromJobState(JobState.toString(j.getState())));
            }

        }
    }

    @Override
    public void jobCreated(UUID id) {
        Job job  = getCoreEngine().getJob(id);
        view.getData().add(new JobData(job));
        runAllJobProperty.set(false);
    }

    @Override
    public void onStateChanged(UUID id, int newState) {
        Job j = getCoreEngine().getJob(id);
        modelWorker.onUpdateJob(j);

        //show notification
        if (newState == JobState.FINISHED) {
            JStesCore.getEventBus().post(new NotificationEvent(NotificationEvent.NotiticationType.INFORMATION,
                    "Job " + j.getName(),
                    "Job \"" + j.getName() + "\" has finished"));
        }
        else if (newState == JobState.ERROR) {
            JStesCore.getEventBus().post(new NotificationEvent(NotificationEvent.NotiticationType.ERROR,
                    "Job " + j.getName(),
                    "Job \"" + j.getName() + "\" has finished with error"));
        }
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


        view.setActionEventHandler("runAllButton", event -> {
            getCoreEngine().executeAll();
        });


        myEventHandler = new HubActionEventHandler(runButtonActionTypeProperty, (TableView) view.getControl(HUB_TABLE_ID));
        view.setActionEventHandler("runJobButton",myEventHandler);

        //setup key events
        view.setKeyEventHandler(HUB_TABLE_ID, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.DELETE);

            @Override
            public void handle(KeyEvent event) {
                if (keyComb1.match(event)) {
//                    this.onComponentEvent(new DefaultComponentEvent(ComponentEvent.JobEventType.DELETE));
                }
            }
        });

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
     * Shutdown the thread of the modelWorker
     */
    public void shutdownWorker() {
        modelWorkderThread.interrupt();
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
     * Perform the delete action
     * @param selectedJobs
     */
    private void onDeleteAction(ObservableList<HubTableModel.JobData> selectedJobs) {

//        deleteService = new DeleteService();
//        deleteService.setJobToDelete(new ArrayList<>(selectedJobs));
//        deleteService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                if ( (Boolean) event.getSource().getValue()) {
////                    JStesCore.getEventBus().post(new DefaultComponentEvent(ComponentEvent.JobEventType.DESELECT));
//                 }
//
//                hubTable.requestFocus();
//                setDeleteInProgress(false);
//
//                HubTableModel.JobData jobData = (HubTableModel.JobData) hubTable.getSelectionModel().getSelectedItem();
//                onJobSelection(jobData);
//
//            }
//        });
//
//        setDeleteInProgress(true);
//        deleteService.start();
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
//            setActionOnButton(runJobButton, getActionFromJobState(data.getStatus()).getActionType());
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
        return (TableView) view.getControl(HUB_TABLE_ID);
    }
//    /**
//     * Delete a list of jobs from source.
//     * Return true if there is no job left in the coreEngine
//     */
//    private class DeleteService extends Service<Boolean> {
//
//        private List<JobData> list;
//
//        public void setJobToDelete(List<JobData> list) {
//            this.list = list;
//        }
//        @Override
//        protected Task<Boolean> createTask() {
//            return new Task<Boolean>() {
//                @Override
//                protected Boolean call() throws Exception {
//
//                    for (JobData jobData : list) {
//                        if (getCoreEngine().deleteJob(UUID.fromString(jobData.getId()))) {
//                            model.deleteJob(jobData);
//                        }
//                    }
//
//                    if (getCoreEngine().count() == 0) {
//                        runAllJobProperty.set(true);
//                        runJobButtonProperty.set(true);
//
//                       setActionOnButton(runJobButton,MyEventHandler.RUN_ACTION);
//                        return true;
//                    }
//
//                    return false;
//                }
//            };
//        }
//    }

}
