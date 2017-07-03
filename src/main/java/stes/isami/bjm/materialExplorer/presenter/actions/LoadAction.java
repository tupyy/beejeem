package stes.isami.bjm.materialExplorer.presenter.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import stes.isami.bjm.materialExplorer.business.MaterialExplorerHandler;

/**
 * Handle load material list action
 */
public class LoadAction implements EventHandler<ActionEvent> {

    private final MaterialExplorerHandler handler;

    public LoadAction(MaterialExplorerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(ActionEvent event) {

    }
}
