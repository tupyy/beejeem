package mainview;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.IIOException;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane rootLayout = null;

        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));

        for(Node node: root.getChildrenUnmodifiable()) {
            if (node instanceof BorderPane) {
                rootLayout = (BorderPane) node;
            }
        }

        rootLayout.setPrefHeight(primaryStage.getHeight());
        rootLayout.setPrefWidth(primaryStage.getWidth());

        showHubView(rootLayout);
        showInfoView(rootLayout);
        showCommandView(rootLayout);
        showStatusBar(rootLayout);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1024, 800));
        primaryStage.show();
    }

    /**
     * Add hub view
     * @param parentNode
     */
    private void showHubView(BorderPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("hub/hub.fxml"));
            BorderPane hubPane = (BorderPane) loader.load();
            parentNode.setCenter(hubPane);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add Detail View
     * @param parentNode
     */
    private void showInfoView(BorderPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("jobDetail/jobDetail.fxml"));
            VBox infoPane = (VBox) loader.load();

            parentNode.setRight(infoPane);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Show command view
     * @param parentNode
     */
    private void showCommandView(BorderPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("command/command.fxml"));
            VBox command = (VBox) loader.load();

            parentNode.setLeft(command);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Show the status bar
     * @param parentNode
     */
    private void showStatusBar(BorderPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("statusBar/statusBar.fxml"));
            HBox statusBar = (HBox) loader.load();

            parentNode.setBottom(statusBar);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
