package stes.isami.bjm.eventbus;

/**
 * Created by cosmin on 29/04/2017.
 */
public class DefaultCoreEvent implements CoreEvent {

    private final CoreEventType eventName;

    public DefaultCoreEvent(CoreEventType eventName) {
        this.eventName = eventName;
    }
    @Override
    public CoreEventType getEventName() {
        return eventName;
    }
}
