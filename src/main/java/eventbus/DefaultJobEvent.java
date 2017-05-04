package eventbus;

import java.util.UUID;

/**
 * Event fired by the {@link main.JStesCore} when an event is received from {@link core.CoreEngine}
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
