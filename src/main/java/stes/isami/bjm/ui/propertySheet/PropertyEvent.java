package stes.isami.bjm.ui.propertySheet;

import javax.swing.event.ChangeEvent;

/**
 * Created by cosmin on 06/05/2017.
 */
public class PropertyEvent extends ChangeEvent {

    private final Object newValue;

    /**
     * Constructs a ChangeEvent object.
     *
     * @param source the Object that is the source of the event
     *               (typically <code>this</code>)
     */
    public PropertyEvent(Object source) {
        super(source);
        newValue = new Object();
    }

    public PropertyEvent(Object source,Object newValue) {
        super(source);
        this.newValue = newValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
