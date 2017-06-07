package stes.isami.bjm.eventbus;

import java.util.List;
import java.util.UUID;

/**
 * Created by tctupangiu on 27/04/2017.
 */
public interface ComponentAction {


    public ComponentActions getAction();

    public ComponentEventHandler getSource();

    public UUID getJobId();

    public List<UUID> getIds();

    public enum ComponentActions {
        EXECUTE,
        EXECUTE_ALL,
        STOP,
        DELETE,
        SELECT,
        DESELECT,
        PREFERENCES_SAVED,
    }


}
