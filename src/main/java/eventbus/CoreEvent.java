package eventbus;

/**
 * Created by cosmin on 29/04/2017.
 */
public interface CoreEvent {

    public enum CoreEventType {
        SSH_CLIENT_CONNECTED,
        SSH_CLIENT_DISCONNECTED,
        SSH_CLIENT_AUTHENTICATED,
        SSH_CLIENT_AUTHENTICATE_ERROR
    }

    /**
     * Get the event action
     * @return
     */
    public CoreEventType getEventName();
}
