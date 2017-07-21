package stes.isami.bjm.ui.hub.presenter;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.ui.hub.logic.HubModel;
import stes.isami.bjm.ui.hub.logic.JobData;
import stes.isami.bjm.eventbus.*;
import stes.isami.bjm.main.JStesCore;

import java.util.List;
import java.util.UUID;

/**
 * CreatorController for the hubView
 */
public class HubControllerImpl extends AbstractComponentEventHandler implements HubController {

    private static final Logger logger = LoggerFactory
            .getLogger(HubControllerImpl.class);

    private final HubView view;
    private final HubModel model;
    private boolean suspendSelection;

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
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/





}
