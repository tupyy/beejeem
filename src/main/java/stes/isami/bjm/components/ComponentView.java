package stes.isami.bjm.components;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

/**
 * Created by cosmin2 on 09/07/2017.
 */
public interface ComponentView<T> {

    /**
     * Get the data
     * @return data
     */
    public ObservableList<T> getData();

    /**
     * Set action event handler
     * @param buttonName buttonID of the button on which the action event is set
     * @param actionEventEventHandler
     */
    public void setActionEventHandler(String buttonName,EventHandler<ActionEvent> actionEventEventHandler);

    /**
     * Set a key event to control. The key event will be bind to a actionevent
     * @param controlID
     * @param bindControlID control whose {@link ActionEvent} will be bind to key event
     * @param keyCode
     */
    public void setKeyEventHandler(String controlID,String bindControlID,KeyCode keyCode);

    /**
     * Set mouse event
     * @param controlName
     * @param mouseEventEventHandler
     */
    public void setMouseEventHandler(String controlName, EventHandler<MouseEvent> mouseEventEventHandler);

    /**
     * Return the control with the id {@code controlID}
     * @param controlID
     * @return
     */
    public Control getControl(String controlID);

}
