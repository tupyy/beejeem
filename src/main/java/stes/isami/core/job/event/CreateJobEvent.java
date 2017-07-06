package stes.isami.core.job.event;

import java.util.UUID;

/**
 * Created by tctupangiu on 06/07/2017.
 */
public class CreateJobEvent extends JobEvent {
    public CreateJobEvent(UUID id) {
        super(id);
    }
}
