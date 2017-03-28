package main;

import com.sun.javafx.application.LauncherImpl;
import core.JStesCore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import preloader.JStesPreloader;
import preloader.TextNotification;

public class MainApp extends Application {

    private final JStesCore jStesCore;
    BooleanProperty ready = new SimpleBooleanProperty(false);


    public MainApp() {

        jStesCore = new JStesCore();

    }

    @Override
    public void init() throws Exception {


    }

    private void longStart() {
        //simulate long init in background
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int max = 10;
                for (int i = 1; i <= max; i++) {
                    Thread.sleep(200);
                    // Send progress to preloader
                    notifyPreloader(new Preloader.ProgressNotification(((double) i)/max));
                    Double d = (double) i/max;
                    notifyPreloader(new TextNotification(String.format("%f",d)));
                }
                // After init is ready, the app is ready to be shown
                // Do this before hiding the preloader stage to prevent the
                // app from exiting prematurely
                ready.setValue(Boolean.TRUE);

                notifyPreloader(new Preloader.StateChangeNotification(
                        Preloader.StateChangeNotification.Type.BEFORE_START));

                return null;
            }
        };
        new Thread(task).start();

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Initiate simulated long startup sequence
        longStart();


        VBox rootLayout = null;

        Parent root = FXMLLoader.load(MainApp.class.getClassLoader().getResource("mainView.fxml"));

        primaryStage.setTitle("JStes");
        primaryStage.setScene(new Scene(root, 1024, 800));

        // After the app is ready, show the stage
        ready.addListener(new ChangeListener<Boolean>(){
            public void changed(
                    ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (Boolean.TRUE.equals(t1)) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            primaryStage.show();
                        }
                    });
                }
            }
        });;

    }



    @Override
    public void stop(){
        System.out.println("App is closing...");
        jStesCore.shutdown();
    }


    public static void main(String[] args) {

        //launch app
   //     Application.launch(MainApp.class,args);
        LauncherImpl.launchApplication(MainApp.class, JStesPreloader.class, args);

    }
}
