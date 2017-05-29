package stes.isami.jstes.eventbus;

import stes.isami.jstes.main.JStesCore;

import java.util.UUID;

/**
 * Event fired by the {@link JStesCore} when an event is received from {@link core.CoreEngine}
 */
public class DefaultJobEvent implements JobEvent {

    private final JobEventType action;
    /**
     * List of jobs
     */
    private UUID jobId = null;

    public DefaultJobEvent(JobEventType action, UUID jobId) {
        this.action = action;
        this.jobId = jobId;
    }


    public UUID getJobId() {
        return jobId;
    }

    @Override
    public JobEventType getAction() {
        return action;
    }

}
