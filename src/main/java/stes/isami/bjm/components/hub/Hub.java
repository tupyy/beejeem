package stes.isami.bjm.components.hub;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import stes.isami.bjm.components.Component;

import java.io.IOException;

/**
 * This class represents the Hub component.
 */
public class Hub implements Component {

    public Hub() {

    }

    @Override
    public Pane getRootPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Hub.class.getClassLoader().getResource("views/hub.fxml"));
        Pane hubPane = (VBox) loader.load();
        return hubPane;
    }
}
