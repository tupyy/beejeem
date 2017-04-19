package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Default component event implementation
 */
public class DefaultComponentEvent extends AbstractComponentEvent {

    /**
     * List of jobs
     */
    private UUID jobId = null;

    public DefaultComponentEvent(Object source, int action,UUID jobId) {
        super(source,action);
        this.jobId = jobId;
    }

    public DefaultComponentEvent(Object source,int action) {
        this(source,action,UUID.randomUUID());
    }

    public UUID getJobId() {
        return jobId;
    }

}
