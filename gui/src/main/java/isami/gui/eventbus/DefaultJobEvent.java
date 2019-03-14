package stes.isami.bjm.eventbus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by tctupangiu on 27/04/2017.
 */
public class DefaultJobEvent implements JobEvent {

    private final JobEventType action;
    private final ComponentEventHandler source;
    private final UUID jobID;
    private final List<UUID> jobIDs;

    public DefaultJobEvent(JobEventType action) {
        this(null,action,UUID.randomUUID());
    }

    public DefaultJobEvent(ComponentEventHandler source, JobEventType action, UUID jobID) {
        this.action = action;
        this.source = source;
        this.jobID = jobID;
        this.jobIDs = Arrays.asList(jobID);
    }

    public DefaultJobEvent(ComponentEventHandler source, JobEventType action, List<UUID> jobIDs) {
        this.action = action;
        this.source = source;
        this.jobIDs = jobIDs;
        this.jobID = jobIDs.get(0);
    }
    @Override
    public JobEventType getEvent() {
        return action;
    }

    @Override
    public ComponentEventHandler getSource() {
        return source;
    }

    @Override
    public UUID getJobId() {
        return jobID;
    }

    @Override
    public List<UUID> getIds() {
        return jobIDs;
    }
}
