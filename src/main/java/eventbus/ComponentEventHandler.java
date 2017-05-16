package eventbus;

import com.google.common.eventbus.Subscribe;

/**
 * This component which are to be register to the EventBus must implement this interface.
 */
public interface ComponentEventHandler {

    /**
     * Method called when a JobEvent is received. The {@code JobEvent} is delivered by the EventBus
     * @param event
     */
    @Subscribe
    public void onJobEvent(JobEvent event);

    @Subscribe
    public void onComponentAction(ComponentAction event);

    @Subscribe
    public void onCoreEvent(CoreEvent event);

}
