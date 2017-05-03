package eventbus;

import main.JStesCore;

/**
 * Created by cosmin on 03/05/2017.
 */
public abstract class AbstractComponentEventHandler implements ComponentEventHandler {

    public AbstractComponentEventHandler() {
        JStesCore.registerController(this);
    }

    @Override
    public void onJobEvent(JobEvent event) {

    }

    @Override
    public void onComponentAction(ComponentAction event) {

    }

    @Override
    public void onCoreEvent(CoreEvent event) {

    }
}
