package stes.isami.core.job.event;

import stes.isami.core.job.JobState;

import java.util.UUID;

/**
 * Created by tctupangiu on 06/07/2017.
 */
public class JobStateChangedEvent extends JobEvent {

    private final int newState;

    public JobStateChangedEvent(UUID id, int newState) {
        super(id);

        this.newState = newState;
    }

    public int getNewState() {
        return newState;
    }
}
