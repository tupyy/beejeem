package stes.isami.bjm.ui.hub.logic;

import stes.isami.core.job.Job;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cosmin on 07/05/2017.
 */
public class ModelWorker implements Runnable {


    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final WorkerActionListener listener;
    private final Map<UUID, JobData> data2;

    private BlockingQueue<MyBeanAction> queue = new LinkedBlockingQueue<>();

    private final ObservableList<JobData> data;

    public ModelWorker(WorkerActionListener listener, ObservableList<JobData> data, Map<UUID, JobData> data2) {
        this.data = data;
        this.data2 = data2;
        this.listener = listener;
    }

    public void onDeleteJob(List<UUID> ids) {
        queue.add(new MyBeanAction(WorkerActionListener.DELETE_ACTION,ids));
    }

    public void onUpdateJob(Job j) {
        queue.add(new MyBeanAction(WorkerActionListener.UPDATE_ACTION,j));
    }

    public void onAddJob(Job job) {
        queue.add(new MyBeanAction(WorkerActionListener.ADD_ACTION,job));
    }

    @Override
    public void run() {

        while (true) {
            try {
                MyBeanAction myBeanAction = queue.take();
                switch (myBeanAction.getAction()) {
                    case WorkerActionListener.DELETE_ACTION:

                        break;
                    case WorkerActionListener.UPDATE_ACTION:
                        for(JobData jobData: data) {
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

    public interface WorkerActionListener {

        static final int DELETE_ACTION = 10;
        static final int UPDATE_ACTION = 20;
        static final int ADD_ACTION = 30;

        /**
         * Called when an action has started
         * @param action
         */
        void onActionStarted(int action);

        /**
         * Called when an action has ended
         * @param action
         */
        void onActionEnded(int action);
    }

    private class MyBeanAction {

        private final int action;
        private final UUID id;
        private final Job job;
        private final List<UUID> ids;

        public MyBeanAction(int action, UUID jobID) {
            this.action = action;
            this.id = jobID;
            this.job = null;
            ids = new ArrayList<>();
        }

        public MyBeanAction(int action, Job job) {
            this.action = action;
            this.job = job;
            this.id = job.getID();
            ids = new ArrayList<>();
        }

        public MyBeanAction(int action,List<UUID> ids) {
            this.action = action;
            this.job = null;
            this.id = ids.get(0);
            this.ids = ids;
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

        public List<UUID> getIds() {
            return ids;
        }
    }
}
