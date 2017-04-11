package gui;

import com.google.common.eventbus.Subscribe;

/**
 * This component which are to be register to the EventBus must implement this interface.
 */
public interface ComponentEventHandler {

    @Subscribe
    public void onComponentEvent(ComponentEvent event);

}
