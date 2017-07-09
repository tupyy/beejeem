package stes.isami.bjm.components.hub;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import stes.isami.bjm.components.ComponentView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import stes.isami.bjm.components.hub.table.HubActionEventHandler;


/**
 * Created by cosmin2 on 09/07/2017.
 */
public interface IHubView<T> extends ComponentView {

    public ObservableList<T> getData();

    public void setActionEventHandler(String buttonName,EventHandler<ActionEvent> actionEventEventHandler);

    public void setKeyEventHandler(String controlName, EventHandler<KeyEvent> keyEventEventHandler);

    public void setMouseEventHandler(String controlName, EventHandler<MouseEvent> mouseEventEventHandler);

    public Control getControl(String controlID);
}
