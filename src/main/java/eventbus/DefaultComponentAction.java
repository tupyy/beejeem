package eventbus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by tctupangiu on 27/04/2017.
 */
public class DefaultComponentAction implements ComponentAction {

    private final ComponentActions action;
    private final ComponentEventHandler source;
    private final UUID jobID;
    private final List<UUID> jobIDs;

    public DefaultComponentAction(ComponentActions action) {
        this(null,action,UUID.randomUUID());
    }

    public DefaultComponentAction(ComponentEventHandler source, ComponentActions action,UUID jobID) {
        this.action = action;
        this.source = source;
        this.jobID = jobID;
        this.jobIDs = Arrays.asList(jobID);
    }

    public DefaultComponentAction(ComponentEventHandler source, ComponentActions action,List<UUID> jobIDs) {
        this.action = action;
        this.source = source;
        this.jobIDs = jobIDs;
        this.jobID = jobIDs.get(0);
    }
    @Override
    public ComponentActions getAction() {
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
