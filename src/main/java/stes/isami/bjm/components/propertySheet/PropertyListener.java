package stes.isami.bjm.components.propertySheet;

import com.google.common.eventbus.Subscribe;

/**
 * Created by cosmin on 04/05/2017.
 */
public interface PropertyListener {

    @Subscribe
    public void parameterUpdated(PropertyEvent propertyEvent);
}
