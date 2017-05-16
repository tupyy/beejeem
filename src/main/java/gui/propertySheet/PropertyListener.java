package gui.propertySheet;

import com.google.common.eventbus.Subscribe;
import core.parameters.Parameter;

import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

/**
 * Created by cosmin on 04/05/2017.
 */
public interface PropertyListener {

    @Subscribe
    public void parameterUpdated(PropertyEvent propertyEvent);
}
