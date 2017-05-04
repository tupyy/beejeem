package eventbus;

import java.util.List;
import java.util.UUID;

/**
 * Default event interface for the component events.
 * <pre>A component event can be a job is selected, deleted or updated</pre>
 */
public interface JobEvent {

    public enum JobEventType {
        JOB_CREATED,
        JOB_DELETED,
        JOB_UPDATED,
        JOB_STOPPED
    }

    /**
     * Get the list of job ids on which the event occured
     * @return
     */
   public UUID getJobId();

    /**
     * Get the event action
     * @return
     */
   public JobEventType getAction();

}
