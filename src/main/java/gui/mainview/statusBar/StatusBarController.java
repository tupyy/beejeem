package gui.mainview.statusBar;

import core.ssh.SshListener;
import eventbus.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.JStesCore;
import main.MainApp;

import java.net.URL;
import java.util.ResourceBundle;

import static main.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 27/03/2017.
 */
public class StatusBarController extends AbstractComponentEventHandler implements Initializable {

    @FXML
    private Label sshLabel;

    @FXML
    private ImageView imageViewer;

    public StatusBarController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        JStesCore.registerController(this);

        if (getCoreEngine().getSshFactory().isConnected() && getCoreEngine().getSshFactory().isAuthenticated()) {
            onClientAuthenticated();
        }
        else {
           onClientDisconnected();
        }
    }

    @Override
    public void onCoreEvent(CoreEvent event) {
        switch (event.getEventName()) {
            case SSH_CLIENT_CONNECTED:
                onClientConnected();
                break;
            case SSH_CLIENT_AUTHENTICATED:
                onClientAuthenticated();
                break;
            case SSH_CLIENT_DISCONNECTED:
                onClientDisconnected();
                break;
        }
    }

    /**
     *
     */
    private void onClientConnected() {
        URL s = MainApp.class.getClassLoader().getResource("images/connected.png");
        final Image image2 = new Image(s.toString());
        imageViewer.setImage(image2);
        sshLabel.setText("Not authenticated");
    }

    private void onClientAuthenticated() {
        URL s = MainApp.class.getClassLoader().getResource("images/connected.png");
        final Image image2 = new Image(s.toString());
        imageViewer.setImage(image2);
        sshLabel.setText("Connected");
    }

    private void onClientDisconnected() {
        URL s = MainApp.class.getClassLoader().getResource("images/disconnected.png");
        final Image image2 = new Image(s.toString());
        imageViewer.setImage(image2);
        sshLabel.setText("Disconnected");
    }
}
