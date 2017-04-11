package gui;

import com.google.common.eventbus.Subscribe;

/**
 * This component which are to be register to the EventBus must implement this interface.
 */
public interface ComponentEventHandler {

    /**
     * Method called when a ComponentEvent is received. The {@code ComponentEvent} is delivered by the EventBus
     * @param event
     */
    @Subscribe
    public void onComponentEvent(ComponentEvent event);

}
