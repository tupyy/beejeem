package main;

import core.JStesCore;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final JStesCore jStesCore;

    public MainApp() {

        jStesCore = new JStesCore();

    }

    @Override
    public void init() throws Exception {
        // Do some heavy lifting
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox rootLayout = null;

        Parent root = FXMLLoader.load(MainApp.class.getClassLoader().getResource("mainView.fxml"));

        primaryStage.setTitle("JStes");
        primaryStage.setScene(new Scene(root, 1024, 800));
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("App is closing...");
        jStesCore.shutdown();
    }


    public static void main(String[] args) {

        //launch app
        Application.launch(MainApp.class,args);
    }
}
