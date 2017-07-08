package stes.isami.bjm.components.hub.table;

import stes.isami.core.job.Job;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cosmin on 07/05/2017.
 */
public class ModelWorker implements Runnable {

    private static final int DELETE_ACTION = 1;
    private static final int UPDATE_ACTION = 2;
    private static final int ADD_ACTION = 3;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private BlockingQueue<MyBeanAction> queue = new LinkedBlockingQueue<>();

    private final ObservableList<HubTableModel.JobData> data;

    public ModelWorker(ObservableList<HubTableModel.JobData> data) {
        this.data = data;
    }

    public void onDeleteJob(UUID id) {
        queue.add(new MyBeanAction(DELETE_ACTION,id));
    }

    public void onUpdateJob(Job j) {
        queue.add(new MyBeanAction(UPDATE_ACTION,j));
    }

    public void onAddJob(Job job) {
        queue.add(new MyBeanAction(ADD_ACTION,job));
    }
    @Override
    public void run() {

        while (true) {
            try {
                MyBeanAction myBeanAction = queue.take();
                switch (myBeanAction.getAction()) {
                    case DELETE_ACTION:
                        logger.info("Delete job: {}",myBeanAction.getId());
                        for (HubTableModel.JobData jobdata : data) {
                            if (jobdata.getId().equals(myBeanAction.getId().toString())) {
                                data.remove(jobdata);
                                break;
                            }
                        }
                        break;
                    case UPDATE_ACTION:
                        for(HubTableModel.JobData jobData: data) {
                            if (jobData.getId().equals(myBeanAction.getId().toString())) {
                                jobData.updateJob(myBeanAction.getJob());
                            }
                        }
                        break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private class MyBeanAction {

        private final int action;
        private final UUID id;
        private final Job job;

        public MyBeanAction(int action, UUID jobID) {
            this.action = action;
            this.id = jobID;
            this.job = null;
        }

        public MyBeanAction(int action, Job job) {
            this.action = action;
            this.job = job;
            this.id = job.getID();
        }

        public int getAction() {
            return action;
        }

        public UUID getId() {
            return id;
        }

        public Job getJob() {
            return job;
        }
    }
}
