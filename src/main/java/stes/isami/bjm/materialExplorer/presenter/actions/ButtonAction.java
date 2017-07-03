package stes.isami.bjm.materialExplorer.presenter.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import stes.isami.bjm.materialExplorer.business.MaterialExplorerHandler;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class ButtonAction implements EventHandler<ActionEvent> {

    private final Actions action;
    private final MaterialExplorerHandler handler;

    public ButtonAction(MaterialExplorerHandler handler, Actions action) {
        this.handler = handler;
        this.action = action;
    }

    @Override
    public void handle(ActionEvent event) {

        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        handler.doAction(action,stage);
    }

    public enum Actions {
        LOAD_MATERIALS,
        IMPORT,
        EXPORT_TO_XML,
        EXPORT_TO_EXCEL,
        CLOSE
    }
}
