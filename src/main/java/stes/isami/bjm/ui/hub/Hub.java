package stes.isami.bjm.ui.hub;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import stes.isami.bjm.ui.UIComponent;
import stes.isami.bjm.ui.hub.logic.HubModelImpl;
import stes.isami.bjm.ui.hub.presenter.HubController;
import stes.isami.bjm.ui.hub.presenter.HubControllerImpl;
import stes.isami.bjm.ui.hub.presenter.HubView;

import java.io.IOException;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * This class represents the Hub component.
 */
public class Hub implements UIComponent {

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
        model.setController(controller);
        view.setController(controller);

        return hubPane;
    }
}
