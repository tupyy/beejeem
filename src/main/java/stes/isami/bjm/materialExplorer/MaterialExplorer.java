package stes.isami.bjm.materialExplorer;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import stes.isami.bjm.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.materialExplorer.presenter.MaterialExplorerController;

import java.io.IOException;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class MaterialExplorer {

    Pane rootPane;
    private MaterialExplorerHandler materialExplorerHandler;
    private MaterialExplorerController controller;

    public MaterialExplorer() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("views/materialExplorer.fxml"));
        rootPane = fxmlLoader.load();
        controller = (MaterialExplorerController) fxmlLoader.getController();

        materialExplorerHandler = new MaterialExplorerHandler(controller);
        controller.setHandler(materialExplorerHandler);
    }

    /**
     * Get the root pane
     * @return
     * @throws NullPointerException
     */
    public Pane getRootPane() throws NullPointerException {
        if (rootPane != null) {
            return rootPane;
        }

        throw new NullPointerException("Root pane not loaded");
    }

    public MaterialExplorerHandler getMaterialExplorerHandler() {
        return materialExplorerHandler;
    }
}
