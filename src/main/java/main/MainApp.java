package main;

import com.sshtools.ssh.SshException;
import com.sun.javafx.application.LauncherImpl;
import core.JStesCore;
import core.configuration.JStesConfiguration;
import core.configuration.JStesPreferences;
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
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import preloader.JStesPreloader;
import preloader.TextNotification;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;

import static core.JStesCore.getCoreEngine;

public class MainApp extends Application {

    private JStesCore jStesCore;
        BooleanProperty ready  = new SimpleBooleanProperty(false);


    public MainApp() {
    }

    @Override
    public void init() throws Exception {


    }

    private void longStart() {
            jStesCore = new JStesCore();

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                notifyPreloader(new TextNotification(String.format("Cleaning the temporary folder")));
                TmpFileCleanup cleanup = new TmpFileCleanup();
                Thread tmpCleanupThread = new Thread(cleanup);
                tmpCleanupThread.setPriority(Thread.MIN_PRIORITY);
                tmpCleanupThread.start();
//                notifyPreloader(new Preloader.ProgressNotification(0.33));

                /**
                 * Read config
                 */
                try {
//                    notifyPreloader(new TextNotification(String.format("Read configuration file...")));
//                    notifyPreloader(new TextNotification(String.format("Configuration file: %s", System.getProperty("user.home") + File.separator + "configuration.xml")));

                    File fh = new File(System.getProperty("user.home") + File.separator + "configuration.xml");

                    JStesConfiguration jStesConfiguration = new JStesConfiguration();
                    jStesConfiguration.loadConfiguration(fh);
//                    notifyPreloader(new Preloader.ProgressNotification(0.5));

                    /**
                     * Connect to ssh
                     */
//                    notifyPreloader(new TextNotification(String.format("Connecting to remote host...")));
                    JStesPreferences preferences = JStesConfiguration.getPreferences();
                    String username = (String) preferences.getUserConfiguration().getParameter("username").getValue();
                    String host = (String) preferences.getUserConfiguration().getParameter("host").getValue();
                    String password = (String) preferences.getUserConfiguration().getParameter("password").getValue();

                    if (username.isEmpty() || host.isEmpty() || password.isEmpty()) {
//                        notifyPreloader(new TextNotification(String.format("Cannot connect to remote host. Username or host or password is missing")));
                    } else {

//                        notifyPreloader(new Preloader.ProgressNotification(0.8));

                        getCoreEngine().getSshFactory().connect(host, username, password);
                        if (getCoreEngine().getSshFactory().isConnected() && getCoreEngine().getSshFactory().isAuthenticated()) {
//                        notifyPreloader(new TextNotification(String.format("Connected to host")));
//                        notifyPreloader(new Preloader.ProgressNotification(1));

                        ready.setValue(Boolean.TRUE);

//                        notifyPreloader(new Preloader.StateChangeNotification(
//                                Preloader.StateChangeNotification.Type.BEFORE_START));
                        }
                    }
                }

                catch (IllegalArgumentException ex) {
//                    notifyPreloader(new TextNotification(String.format("Connect to remote host. ".concat(ex.getMessage()))));
                }
                catch (SSLException e) {
//                    notifyPreloader(new TextNotification(String.format("Connect to remote host. ".concat(e.getMessage()))));
                }
                catch (IOException e) {
//                    notifyPreloader(new TextNotification(String.format("ERROR: %s",e.getMessage())));
                }
                catch (NullPointerException e) {
//                    notifyPreloader(new TextNotification(String.format("ERROR: %s",e.getMessage())));
                }

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
        primaryStage.show();

//        // After the app is ready, show the stage
//        ready.addListener(new ChangeListener<Boolean>(){
//            public void changed(
//                    ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
//                if (Boolean.TRUE.equals(t1)) {
//                    Platform.runLater(new Runnable() {
//                        public void run() {
//
//                        }
//                    });
//                }
//            }
//        });;

    }



    @Override
    public void stop(){
        System.out.println("App is closing...");
        jStesCore.shutdown();
    }


    public static void main(String[] args) {

        //launch app
        Application.launch(MainApp.class,args);
//        LauncherImpl.launchApplication(MainApp.class, JStesPreloader.class, args);

    }
}
