package stes.isami.bjm.ui.materialExplorer;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import stes.isami.bjm.ui.UIComponent;
import stes.isami.bjm.ui.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.ui.materialExplorer.presenter.MaterialExplorerController;

import java.io.IOException;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class MaterialExplorer implements UIComponent{

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
    @Override
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
