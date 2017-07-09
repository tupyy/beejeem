package stes.isami.bjm.eventbus;

import stes.isami.bjm.main.JStesCore;

/**
 * Created by cosmin on 03/05/2017.
 */
public abstract class AbstractComponentEventHandler implements ComponentEventHandler {

    public AbstractComponentEventHandler() {
        JStesCore.registerController(this);
    }

    @Override
    public void onComponentEvent(ComponentEvent event) {

    }

    @Override
    public void onCoreEvent(CoreEvent event) {

    }
}
