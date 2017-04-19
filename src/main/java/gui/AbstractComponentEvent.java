package gui;

import java.util.List;
import java.util.UUID;

/**
 * Abstract implentation of the interface ComponentEvent
 */
public class AbstractComponentEvent implements ComponentEvent {


    private int action;
    private Object source;

    public AbstractComponentEvent(Object source,int action) {
        this.action = action;
        this.source = source;
    }

    @Override
    public UUID getJobId() {
        return null;
    }

    @Override
    public int getAction() {
        return action;
    }
}
