package gui;

import java.util.List;
import java.util.UUID;

/**
 * Default event interface for the component events.
 * <pre>A component event can be a job is selected, deleted or updated</pre>
 */
public interface ComponentEvent  {

    public static final int JOB_SELECTED = 1;

    public static final int JOB_DELETED = 2;

    public static final int SELECTION_CLEARED = 3;

    /**
     * Get the list of job ids on which the event occured
     * @return
     */
   public UUID getJobId();

    /**
     * Get the event action
     * @return
     */
   public int getAction();

}
