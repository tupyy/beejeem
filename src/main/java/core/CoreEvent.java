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
public enum CoreEvent {
    SSH_CONNECTION_ERROR;
}
