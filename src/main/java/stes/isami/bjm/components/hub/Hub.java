package stes.isami.bjm.components.hub;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import stes.isami.bjm.components.Component;
import stes.isami.bjm.components.hub.logic.HubModel;
import stes.isami.bjm.components.hub.logic.HubModelImpl;
import stes.isami.bjm.components.hub.presenter.HubController;
import stes.isami.bjm.components.hub.presenter.HubControllerImpl;
import stes.isami.bjm.components.hub.presenter.HubView;

import java.io.IOException;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * This class represents the Hub component.
 */
public class Hub implements Component {

    private final HubModelImpl model;
    private HubController controller;

    public Hub() {
        model = new HubModelImpl();
        getCoreEngine().addJobListener(model);
    }

    @Override
    public Pane getRootPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Hub.class.getClassLoader().getResource("views/hub.fxml"));
        Pane hubPane = loader.load();
        HubView view = (HubView) loader.getController();

        controller = new HubControllerImpl(model,view);
        view.setController(controller);

        return hubPane;
    }
}
