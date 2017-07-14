package stes.isami.bjm.components.hub.presenter;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.components.hub.logic.HubModel;
import stes.isami.bjm.components.hub.logic.JobData;
import stes.isami.bjm.components.notifications.NotificationEvent;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobEvent;
import stes.isami.core.job.JobState;
import stes.isami.bjm.eventbus.*;
import stes.isami.bjm.components.jobinfo.JobInfo;
import stes.isami.bjm.main.JStesCore;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;
import static stes.isami.bjm.main.JStesCore.getEventBus;

/**
 * CreatorController for the hubView
 */
public class HubControllerImpl extends AbstractComponentEventHandler implements HubController {

    private static final Logger logger = LoggerFactory
            .getLogger(HubControllerImpl.class);

    private final HubView view;
    private final HubModel model;

    public HubControllerImpl(HubModel model, HubView view) {
        super();

        this.model = model;
        this.view = view;
    }


    @Override
    public void onCoreEvent(CoreEvent event) {
        if (event.getEventName() == CoreEvent.CoreEventType.SHUTDOWN) {
            model.shutdown();
            view.shutdown();
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED) {
            view.onSshDisconnect();
        }
        else if (event.getEventName() == CoreEvent.CoreEventType.SSH_CLIENT_AUTHENTICATED) {
           view.onSshAuthenticated();
        }
    }

    /**
     * Perform action when a job has been selected in the hubTable
     * @param id id of the selected job
     */
    public void onJobSelection(UUID id) {
        JStesCore.getEventBus().post(new DefaultComponentEvent(HubControllerImpl.this, ComponentEvent.JobEventType.SELECT, id));
    }

    @Override
    public ObservableList<JobData> getData() {
        return model.getData();
    }


    @Override
    public void onActionPerformed(int action) {

        List<UUID> selectedJobs = view.getSelectedJobs();
        switch (action) {
            case HubView.RUN_JOB_ACTION:
                model.executeJob(selectedJobs);
                break;
            case HubView.DELETE_ACTION:
                model.deleteJobs(selectedJobs);
                break;
            case HubView.RUN_ALL_ACTION:
                model.executeAll();
                break;

        }
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/




}
