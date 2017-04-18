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
    private List<UUID> jobIds = null;

    public DefaultComponentEvent(Object source, int action, List<UUID> jobIds) {
        super(source,action);
        this.jobIds = jobIds;
    }

    public DefaultComponentEvent(Object source,int action) {
        this(source,action,new ArrayList<>());
    }

    public DefaultComponentEvent(Object source,int action,UUID jobID) {
        this(source,action,new ArrayList<>());
        jobIds.add(jobID);

    }

    public List<UUID> getJobIds() {
        return jobIds;
    }

}
