package stes.isami.bjm.components.hub.table;

import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;
import stes.isami.core.parameters.ParameterSet;
import javafx.beans.property.SimpleStringProperty;
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
public class HubTableModel {

    public static final int DELETE_STARTED = 0;
    public static final int DELETE_ENDED = 1;

    private ObservableList<JobData> data = FXCollections.observableArrayList();

    private ModelWorker modelWorker;
    private Thread modelWorkderThread;

    public HubTableModel() {

        modelWorker = new ModelWorker(data);
        modelWorkderThread= new Thread(modelWorker);
        modelWorkderThread.start();
    }

    public void addJob(Job j) {
        getData().add(new JobData(j));
    }

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
        for (JobData jobData: data) {
            if (jobData.getId().equals(id.toString())) {
                return jobData;
            }
        }

        return null;
    }

    public void updateState(UUID id, int newState) {
        JobData jobData = getJobData(id);
        if (jobData != null) {
            jobData.status.setValue(JobState.toString(newState));
        }
    }
    //<editor-fold desc="Job Data">
    /**
     * Class which defines the model for the hub table
     */
    public class JobData {

        private SimpleStringProperty batchID;
        private SimpleStringProperty name;
        private SimpleStringProperty destinationFolder;
        private SimpleStringProperty type;
        private SimpleStringProperty localFolder;
        private SimpleStringProperty status;
        private SimpleStringProperty id;

        public JobData(Job job) {

            try {
                ParameterSet parameterSet = job.getParameters();
                this.name = new SimpleStringProperty(getParameterValue(parameterSet,"name"));
                this.destinationFolder = new SimpleStringProperty(getParameterValue(parameterSet,"destinationFolder"));

                /**
                 * Fix
                 */

                try {
                    this.type = new SimpleStringProperty(parameterSet.getParameter("type").getDescription());
                }
                catch (IllegalArgumentException ex) {
                    this.type = new SimpleStringProperty("Unknown");
                }

                this.localFolder = new SimpleStringProperty(getParameterValue(parameterSet,"localFolder"));
                this.batchID = new SimpleStringProperty(getParameterValue(parameterSet,"batchID"));
                this.status = new SimpleStringProperty(JobState.toString(job.getState()));
                this.id = new SimpleStringProperty(job.getID().toString());
            }
            catch (IllegalArgumentException ex) {

            }
        }

        public void updateJob(Job j) {
            this.name.set((String) j.getParameters().getParameter("name").getValue());
            this.destinationFolder.set((String) j.getParameters().getParameter("destinationFolder").getValue());
            this.localFolder.set((String) j.getParameters().getParameter("localFolder").getValue());
            this.status.set(JobState.toString(j.getState()));
            this.batchID.set(getParameterValue(j.getParameters(),"batchID"));
        }


        public String getName() {
            return name.get();
        }

        public String getDestinationFolder() {
            return destinationFolder.get();
        }

        public String getType() {
            return type.get();
        }

        public String getLocalFolder() {
            return localFolder.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getId() {
            return id.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }
        public SimpleStringProperty destinationFolderProperty() {
            return destinationFolder;
        }

        public SimpleStringProperty batchIDProperty() {
            return batchID;
        }
        public SimpleStringProperty statusProperty() { return status;};
        public SimpleStringProperty localFolderProperty() {return localFolder;}

        private String getParameterValue(ParameterSet parameters, String parameterName) {
            try {
                return parameters.getParameter(parameterName).getValue().toString();
            }
            catch (IllegalArgumentException ex) {
                return "";
            }
        }

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
