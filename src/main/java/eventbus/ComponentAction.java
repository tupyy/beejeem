package eventbus;

import java.util.UUID;

/**
 * Created by tctupangiu on 27/04/2017.
 */
public interface ComponentAction {


    public ComponentActions getAction();

    public ComponentEventHandler getSource();

    public UUID getJobId();

    public enum ComponentActions {
        EXECUTE,
        EXECUTE_ALL,
        STOP,
        DELETE,
        SELECT
    }


}
