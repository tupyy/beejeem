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

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox rootLayout = null;

        Parent root = FXMLLoader.load(MainApp.class.getClassLoader().getResource("mainView.fxml"));

        for(Node node: root.getChildrenUnmodifiable()) {
            if (node instanceof VBox) {
                rootLayout = (VBox) node;
            }
        }

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1024, 800));
        primaryStage.show();
    }




}
