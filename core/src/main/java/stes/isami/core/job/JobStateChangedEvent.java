package stes.isami.core.job;

import java.util.UUID;

/**
 * Created by cosmin2 on 09/07/2017.
 */
public class JobStateChangedEvent extends JobEvent {

    public JobStateChangedEvent(UUID id) {
        super(id,JobEventType.STATE_CHANGED);
    }
}
