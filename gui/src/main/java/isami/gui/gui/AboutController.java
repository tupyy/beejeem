package stes.isami.bjm.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import stes.isami.bjm.main.MainApp;
import preloader.JStesPreloader;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tctupangiu on 16/05/2017.
 */
public class AboutController implements Initializable {

    @FXML private VBox labelPane;
    @FXML private Button closebutton;
    @FXML private ImageView logoImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Label text = new Label();
        text.setText("\n\n" +
                "Batch Job Manager is free software, which is licensed \nto you under the " +
                "GNU General Public License, version 2. \nPlease see the file " +
                "LICENSE for more details, or visit 'www.gnu.org'.\n" +
                "\n" +
                "This software is provided AS-IS, with ABSOLUTELY NO WARRANTY.\n" +
                "\n" +
                "YOU ASSUME ALL RESPONSIBILITY FOR ANY AND ALL CONSEQUENCE\n" +
                "THAT MAY RESULT FROM THE USE OF THIS SOFTWARE!");

        Label textArea = new Label("Batch System Job Launcher " + MainApp.getVersion()+	"\n" +
                "          Created by Cosmin Tupangiu\n");
        labelPane.getChildren().add(textArea);
        labelPane.getChildren().add(text);

        String image = AboutController.class.getClassLoader().getResource("images/small_logo.png").toExternalForm();
        Image img = new Image(image);

        logoImageView.setImage(img);

        closebutton.setOnAction(event -> {
            // close the dialog.
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        });

    }
}
