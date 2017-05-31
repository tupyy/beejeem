package stes.isami.bjm.gui.mainview.statusBar;

import com.google.common.eventbus.Subscribe;
import com.sshtools.ssh.SshException;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.configuration.Preferences;
import stes.isami.bjm.eventbus.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import stes.isami.bjm.main.JStesCore;
import stes.isami.bjm.main.MainApp;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.tcpserver.TcpEvent;

import java.net.URL;
import java.util.ResourceBundle;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 27/03/2017.
 */
public class StatusBarController implements Initializable, ComponentEventHandler {

    @FXML
    private Label sshLabel;

    @FXML
    private ImageView imageViewer;

    @FXML private HBox connectHBox;
    @FXML private HBox masterPane;
    @FXML private Label tcpServerStatusLabel;

    private PopOver popOver = null;
    private Button connectButton;
    private Label popupLabel;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        JStesCore.registerController(this);

        if (getCoreEngine().getSshFactory().isConnected() && getCoreEngine().getSshFactory().isAuthenticated()) {
            onClientAuthenticated();
        }
        else {
           onClientDisconnected();
        }

        connectHBox.setOnMouseClicked(event -> {
            if (popOver != null && popOver.isShowing()) {
                popOver.hide();
                popOver = null;
            }
            else if (popOver == null){
                popOver = createPopOver();
                if (getCoreEngine().getSshFactory().isConnected() && getCoreEngine().getSshFactory().isAuthenticated()) {
                    popupLabel.setText("Connected to remote host");
                    connectButton.setText("Disconnect");
                }
                else {
                    popupLabel.setText("Disconnected");
                    connectButton.setText("Connect");
                }

                    popOver.show(connectHBox);
            }
            else {
                if (popOver.isShowing()) {
                    closePopUp();
                }
            }
        });
    }

    @Override
    public void onJobEvent(JobEvent event) {

    }

    @Override
    public void onComponentAction(ComponentAction event) {

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
            case SHUTDOWN:
                logger.info("Popup closing..");
                if (popOver != null) {
                    popOver.hide();
                    popOver = null;
                    logger.info("Popup closed");
                }
                break;
        }
    }

    @Subscribe
    public void onTcpServerEvent(TcpEvent tcpEvent) {

        Platform.runLater(() -> {
            switch (tcpEvent.getEventName()) {
                case TCP_CLIENT_CONNECTED:
                    tcpServerStatusLabel.setText("Client connected");
                    logger.info("Excel client connected");
                    break;
                case TCP_CLIENT_DISCONNECTED:
                    tcpServerStatusLabel.setText("Excel client disconnected");
                    logger.info("Client disconnected");
                    break;
                case RECEIVING_STARTED:
                    tcpServerStatusLabel.setText("Receiving data...");
                    break;
            }
        });

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

    private PopOver createPopOver() {

        VBox vBox = new VBox();

        connectButton = new Button("Connect");
//        connectButton.setPrefWidth(70);
        popupLabel = new Label("Connected");

        connectButton.setOnAction(new MyHandler(this));
        VBox.setMargin(connectButton,new Insets(10,10,10,10));
        VBox.setMargin(popupLabel,new Insets(10,10,0,10));
        vBox.setPrefSize(200,50);
        vBox.setAlignment(Pos.BOTTOM_CENTER);
        vBox.getChildren().addAll(popupLabel,connectButton);

        PopOver popOver = new PopOver(vBox);
        popOver.setDetached(false);
        popOver.arrowSizeProperty().set(10);
        popOver.arrowIndentProperty().set(10);   
        popOver.arrowLocationProperty().set(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.cornerRadiusProperty().set(10);
        popOver.setHeaderAlwaysVisible(false);
        popOver.setAnimated(true);

        return popOver;
    }

    private void closePopUp() {
        if(popOver != null) {
            popOver.hide();
            popOver = null;
        }
    }
    private class MyHandler implements EventHandler<ActionEvent> {

        private final ComponentEventHandler parent;

        public MyHandler(ComponentEventHandler parent) {
            this.parent = parent;
        }
        @Override
        public void handle(ActionEvent event) {
            if (JStesCore.getCoreEngine().getSshFactory().isAuthenticated()) {
                getCoreEngine().getSshFactory().disconnect();
            }
            else {
                try {
                    Preferences preferences = JStesConfiguration.getPreferences();
                    getCoreEngine().getSshFactory().connect(preferences.getValue("host"),preferences.getValue("username"),preferences.getValue("password"));
                } catch (SshException e) {
                    JStesCore.getEventBus().post(new DefaultCoreEvent(CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED));
                }
            }
            closePopUp();
        }
    }
}
