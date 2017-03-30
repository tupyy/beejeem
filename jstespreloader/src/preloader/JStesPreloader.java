package preloader;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Preloader class for main app. It reads the configuration file and connect to remote host.
 */
public class JStesPreloader extends Preloader {

    ProgressBar progressBar;
    TextArea logArea;
    VBox pane;

    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        VBox loading = new VBox(20);
        loading.setMaxWidth(Region.USE_COMPUTED_SIZE);
        loading.setMaxHeight(Region.USE_COMPUTED_SIZE);

        logArea = new TextArea();
        String image = JStesPreloader.class.getClassLoader().getResource("images/logo_splash.png").toExternalForm();

        logArea.getStylesheets().add(JStesPreloader.class.getClassLoader().getResource("textarea.css").toExternalForm());
        logArea.setEditable(false);
        logArea.setText("Loading app...");
        VBox.setVgrow(logArea, Priority.ALWAYS);
        logArea.setMaxWidth(Region.USE_COMPUTED_SIZE);
        loading.getChildren().add(logArea);

        progressBar = new ProgressBar();
        progressBar.setMaxWidth(Region.USE_COMPUTED_SIZE);
        progressBar.setPrefWidth(584);
        progressBar.setPrefHeight(20);
        loading.getChildren().add(progressBar);


        BorderPane root = new BorderPane(loading);
        Scene scene = new Scene(root);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setWidth(584);
        primaryStage.setHeight(555);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {

    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof ProgressNotification) {
             progressBar.setProgress( ((ProgressNotification) pn).getProgress());
        }
        else if(pn instanceof TextNotification) {
            logArea.setText(logArea.getText() + "\n" + ((TextNotification) pn).getNotification());
        }
        else if (pn instanceof StateChangeNotification) {
            StateChangeNotification sn = (StateChangeNotification) pn;
            if (sn.getType() == StateChangeNotification.Type.BEFORE_START) {
                stage.hide();
            }
        }
    }

}
