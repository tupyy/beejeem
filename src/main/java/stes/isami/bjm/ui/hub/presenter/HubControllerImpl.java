package stes.isami.bjm.ui.hub.presenter;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.ui.hub.logic.HubModel;
import stes.isami.bjm.ui.hub.logic.JobData;
import stes.isami.bjm.eventbus.*;
import stes.isami.bjm.main.JStesCore;

import java.util.*;

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
                if (selectedJobs.size() > 0) {
                    model.executeJob(selectedJobs);
                }
                break;
            case HubView.DELETE_ACTION:
                if (selectedJobs.size() > 0) {
                    model.deleteJobs(selectedJobs);
                }
                break;
            case HubView.RUN_ALL_ACTION:
                model.executeAll();
                break;

        }
    }

    @Override
    public void onStartDelete() {
        view.onStartDeletion();
    }

    @Override
    public void onEndDelete() {
        view.onEndDeletion();
        List<UUID> selection = view.getSelectedJobs();
        if (selection.size() > 0) {
            JStesCore.getEventBus().post(new DefaultComponentEvent(this, ComponentEvent.JobEventType.SELECT,selection.get(0)));
        }
        else {
            JStesCore.getEventBus().post(new DefaultComponentEvent(this, ComponentEvent.JobEventType.DESELECT,UUID.randomUUID()));
        }
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/





}
