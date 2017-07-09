package stes.isami.bjm.eventbus;

import java.util.List;
import java.util.UUID;

/**
 * Created by tctupangiu on 27/04/2017.
 */
public interface ComponentEvent {


    public JobEventType getEvent();

    public ComponentEventHandler getSource();

    public UUID getJobId();

    public List<UUID> getIds();

    public enum JobEventType {
        DELETE,
        SELECT,
        DESELECT
    }


}
