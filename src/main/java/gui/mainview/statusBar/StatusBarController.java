package gui.mainview.statusBar;

import core.ssh.SshListener;
import gui.MainController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.MainApp;

import javax.xml.ws.WebFault;
import java.net.URL;
import java.util.ResourceBundle;

import static core.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 27/03/2017.
 */
public class StatusBarController implements Initializable,SshListener {

    @FXML
    private Label sshLabel;

    @FXML
    private ImageView imageViewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        getCoreEngine().getSshFactory().addSshEventListener(this);

        if (getCoreEngine().getSshFactory().isConnected() && getCoreEngine().getSshFactory().isAuthenticated()) {
            URL s = MainApp.class.getClassLoader().getResource("images/connected.png");
            final Image image2 = new Image(s.toString());
            imageViewer.setImage(image2);
            sshLabel.setText("Connected");
        }
        else {
            URL s = MainApp.class.getClassLoader().getResource("images/disconnected.png");
            final Image image2 = new Image(s.toString());
            imageViewer.setImage(image2);
            sshLabel.setText("Disconnected");
        }


    }

    @Override
    public void channelClosed() {

    }

    @Override
    public void channelClosing() {

    }

    @Override
    public void connected() {
        sshLabel.setText("Not authenticated");
    }

    @Override
    public void authenticated() {
        URL s = MainApp.class.getClassLoader().getResource("images/connected.png");
        final Image image2 = new Image(s.toString());
        imageViewer.setImage(image2);
        sshLabel.setText("Connected");
    }

}
