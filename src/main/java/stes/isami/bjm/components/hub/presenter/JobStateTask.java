package stes.isami.bjm.components.hub.presenter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.components.hub.logic.JobData;
import stes.isami.core.job.JobState;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class check the state of each job to determine if the buttons should be enable or disable
 */
public class JobStateTask implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(JobStateTask.class);

    private final SimpleBooleanProperty disableRunProperty;
    private final SimpleBooleanProperty disableStopProperty;
    private final LinkedBlockingQueue<List<JobData>> queue = new LinkedBlockingQueue<>();
    private List<JobData> selectedJobs;

    public JobStateTask(SimpleBooleanProperty disableRunProperty,
                        SimpleBooleanProperty disableStopProperty) {
         this.disableRunProperty = disableRunProperty;
        this.disableStopProperty = disableStopProperty;
    }

    @Override
    public void run() {
        List<JobData> myList;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                myList = queue.take();
                boolean currentState = true;
                for (JobData jobData : myList) {
                    boolean jobState = isJobExecutable(jobData.getStatus());
                    if (jobState) {
                        currentState = false;
                        break;
                    }
                }

                disableRunProperty.set(currentState);

                currentState = true;
                for (JobData jobData : myList) {
                    boolean jobState = isJobExecutable(jobData.getStatus());
                    if (!jobState) {
                        currentState = false;
                        break;
                    }
                }
                disableStopProperty.set(currentState);
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Wrapper to start method.
     * @param selectedJob selected jobs
     */
    public void setSelectionList(List<JobData> selectedJob) {
        try {
            queue.put(selectedJob);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Check if a job can be executed
     * @param state
     * @return true if the job can be started, false otherwise
     */
    private boolean isJobExecutable(String state) {

        if (state.equals("Ready") || state.equals("Stop")
                || state.equals("Finished")
                || state.equals("Error")) {
            return true;
        }

        return false;
    }


}
