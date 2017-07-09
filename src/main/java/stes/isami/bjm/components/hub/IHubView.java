package stes.isami.bjm.components.hub;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import stes.isami.bjm.components.ComponentView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


/**
 * Created by cosmin2 on 09/07/2017.
 */
public interface IHubView<T> extends ComponentView {

    public ObservableList<T> getData();

    public void setActionEventHandler(String buttonName,EventHandler<ActionEvent> actionEventEventHandler);

    /**
     * Set a key event to control. The key event will be bind to a actionevent
     * @param controlID
     * @param bindControlID control whose {@link ActionEvent} will be bind to key event
     * @param keyCode
     */
    public void setKeyEventHandler(String controlID,String bindControlID,KeyCode keyCode);

    public void setMouseEventHandler(String controlName, EventHandler<MouseEvent> mouseEventEventHandler);

    public Control getControl(String controlID);
}
