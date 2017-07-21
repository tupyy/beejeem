package stes.isami.bjm.ui.hub.logic;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stes.isami.bjm.ui.hub.presenter.HubController;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Implementation of the {@link HubModel}
 */
public class HubModelImpl implements HubModel,JobListener {

    private HubController controller;

    private ObservableList<JobData> data = FXCollections.observableArrayList();
    private Map<UUID,JobData> data2 = new HashMap<>();

    public HubModelImpl() {
    }

    public void setController(HubController controller) {
        this.controller = controller;
    }

    @Override
    public ObservableList<JobData> getData() {
        return data;
    }

    @Override
    public void executeJob(List<UUID> jobIDs) {
        jobIDs.forEach(id -> getCoreEngine().executeJob(id));
    }

    @Override
    public void executeAll() {
        getCoreEngine().executeAll();
    }

    @Override
    public void stopJob(List<UUID> jobIDs) {
        jobIDs.forEach(id -> getCoreEngine().stopJob(id));
    }

    @Override
    public void deleteJobs(List<UUID> jobIDs) {
        getCoreEngine().deleteJobs(jobIDs);
    }

    @Override
    public void onJobEvent(JobEvent event) {
        Job job;
        JobData jobData;
        switch (event.getEventType()) {
            case CREATE:
                job  = getCoreEngine().getJob(event.getId());
                jobData = new JobData(job);
                data.add(jobData);
                data2.put(job.getID(),jobData);
                break;
            case UPDATE:
            case STATE_CHANGED:
                job = getCoreEngine().getJob(event.getId());
                Platform.runLater(() -> {
                    final JobData jd = data2.get(event.getId());
                    jd.updateJob(job);
                });
                break;
            case DELETE:
                Platform.runLater(() -> {
                    controller.onStartDelete();
                    for (UUID id: event.getIds()) {
                        final JobData jd = data2.get(id);
                        data.remove(jd);
                        data2.remove(id);
                    }
                    controller.onEndDelete();
                });
                 break;
            case START_DELETE:
                controller.onStartDelete();
                break;
            case END_DELETE:
                controller.onEndDelete();
        }
    }

    @Override
    public void shutdown() {
    }

}
