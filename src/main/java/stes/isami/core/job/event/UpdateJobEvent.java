package stes.isami.core.job.event;

import java.util.UUID;

/**
 * Created by tctupangiu on 06/07/2017.
 */
public class UpdateJobEvent extends JobEvent {

    public UpdateJobEvent(UUID id) {
        super(id);
    }
}
