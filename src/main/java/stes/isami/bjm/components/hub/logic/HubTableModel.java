package stes.isami.bjm.components.hub.logic;

import stes.isami.core.job.Job;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.*;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class HubTableModel  {

    public static final int DELETE_STARTED = 0;
    public static final int DELETE_ENDED = 1;

    private ObservableList<JobData> data = FXCollections.observableArrayList();

    private ModelWorker modelWorker;
    private Thread modelWorkderThread;

    public HubTableModel() {


    }

//    public void addJob(Job j) {
//        getData().add(new JobData(j));
//    }

    public void updateJob(Job j) {
        modelWorker.onUpdateJob(j);
    }

    public ObservableList<JobData> getData() {
        return data;
    }

    public void shutdown() {
        modelWorkderThread.interrupt();
    }
    /**
     * Delete a job from model
     * @param jobData
     */
    public void deleteJob(JobData jobData) {
        data.remove(jobData);
    }

    public void deleteJobs(List<JobData> jobDataList) {

        DeleteService deleteService = new DeleteService();
        deleteService.setData(data);
        deleteService.setIDList(jobDataList);
        deleteService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

            }
        });
        deleteService.start();
    }

    public JobData getJobData(UUID id) {
//        for (JobData jobData: data) {
//            if (jobData.getId().equals(id.toString())) {
//                return jobData;
//            }
//        }
//
       return null;
    }

    public void updateState(UUID id, int newState) {
        JobData jobData = getJobData(id);
        if (jobData != null) {
//            jobData.status.setValue(JobState.toString(newState));
        }
    }
    //<editor-fold desc="Job Data">
    /**
     * Class which defines the model for the hub table
     */
    public class JobData {



    }
    //</editor-fold>

    private class DeleteService extends Service<Integer> {

        private List<JobData> idList;
        private ObservableList<JobData> data;

        public void setIDList(List<JobData> idList) {
            this.idList = idList;
        }

        public void setData(ObservableList<JobData> data) {
            this.data = data;
        }

        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                int jobRemoved = 0;

                @Override
                protected Integer call() throws Exception {
                    if (data !=null && idList != null) {
                        for (HubTableModel.JobData jobdata : idList) {
                             data.remove(jobdata);
                            jobRemoved++;

                        }

                        return jobRemoved;
                    }
                    else {
                        return -1;
                    }
                }
            };
        }
    }

}
