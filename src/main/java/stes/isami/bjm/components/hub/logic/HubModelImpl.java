package stes.isami.bjm.components.hub.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobEvent;

import java.util.List;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class HubModelImpl implements HubModel,JobListener {

    public static final int DELETE_STARTED = 0;
    public static final int DELETE_ENDED = 1;

    private ObservableList<JobData> data = FXCollections.observableArrayList();

    private ModelWorker modelWorker;
    private Thread modelWorkderThread;

    public HubModelImpl() {

        //start the worker
        modelWorker = new ModelWorker(data);
        modelWorkderThread= new Thread(modelWorker);
        modelWorkderThread.start();

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
        switch (event.getEventType()) {
            case CREATE:
                job  = getCoreEngine().getJob(event.getId());
                data.add(new JobData(job));
                break;
            case UPDATE:
                job = getCoreEngine().getJob(event.getId());
                modelWorker.onUpdateJob(job);
                break;
            case STATE_CHANGED:
                job = getCoreEngine().getJob(event.getId());
                modelWorker.onUpdateJob(job);
                break;
            case DELETE:
                modelWorker.onDeleteJob(event.getId());
                break;
        }
    }

    @Override
    public void shutdown() {
        modelWorkderThread.interrupt();
    }


}
