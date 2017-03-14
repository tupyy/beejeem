/*
 * Events for a job
 * Every action on a job: creation,editing,removing,running is 
 * handled by this class
 */
package core;

import java.util.EventObject;
import java.util.UUID;

/**
 * Event class for the Core
 */
public class CoreEvent extends EventObject {
    private UUID id;
    private final CoreEventType action;
    
    public CoreEvent(Object source, CoreEventType action, UUID id) {
        super(source);
        this.action = action;
        this.id = id;
    }

    /**
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the action
     */
    public CoreEventType getAction() {
        return action;
    }    
}
