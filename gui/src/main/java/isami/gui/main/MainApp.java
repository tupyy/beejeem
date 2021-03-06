package stes.isami.bjm.main;

import com.sshtools.ssh.SshException;
import com.sun.javafx.application.LauncherImpl;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.configuration.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import preloader.JStesPreloader;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class MainApp extends Application {

    private JStesCore jStesCore;
        BooleanProperty ready  = new SimpleBooleanProperty(false);

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static String version = new String();

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

                version = getManifestInfo();

                /**
                 * Read config
                 */
                try {

                    File fh = new File(System.getProperty("user.home") + File.separator + "configuration.xml");

                    JStesConfiguration jStesConfiguration = new JStesConfiguration();
                    jStesConfiguration.loadConfiguration(fh);
                    notifyPreloader(new Preloader.ProgressNotification(0.5));

                    Preferences preferences = JStesConfiguration.getPreferences();
                    /**
                     * Load plugins
                     */
                    try {
                        String pluginPath = (String) preferences.getValue("pluginFolder");
                        JStesCore.getCoreEngine().loadPlugins(pluginPath);
                    }
                    catch (IllegalArgumentException ex) {

                    }
                    catch (IOException ex) {

                    }

                    /**
                     * Connect to ssh
                     */

                    String username = preferences.getValue("username");
                    String host =  preferences.getValue("host");
                    String password =  preferences.getValue("password");

                    notifyPreloader(new Preloader.ProgressNotification(0.8));

                    try {
                        JStesCore.getCoreEngine().getSshFactory().connect(host, username, password);
                    }
                    catch (IllegalArgumentException | SshException ex) {
                        logger.error(ex.getMessage());
                    }

                    notifyPreloader(new Preloader.ProgressNotification(1));

                    ready.setValue(Boolean.TRUE);

                    notifyPreloader(new Preloader.StateChangeNotification(
                            Preloader.StateChangeNotification.Type.BEFORE_START));

                }

                catch (IllegalArgumentException | SSLException ex) {
                    ex.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
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

        primaryStage.setTitle("Batch Job Manager");
        Scene rootScene = new Scene(root, 1024, 800);
        rootScene.getStylesheets().add(MainApp.class.getClassLoader().getResource("css/mainapp.css").toExternalForm());
        primaryStage.setScene(rootScene);

//        // After the app is ready, show the stage
        ready.addListener(new ChangeListener<Boolean>(){
            public void changed(
                    ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (Boolean.TRUE.equals(t1)) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            if ( !version.isEmpty() ) {
                                primaryStage.setTitle(primaryStage.getTitle() + " " + version);
                            }
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
       // Application.launch(MainApp.class,args);
        LauncherImpl.launchApplication(MainApp.class, JStesPreloader.class, args);

    }

    public static String getVersion() {
        return version;
    }
    public String getManifestInfo() {
        Enumeration resEnum;
        try {
            resEnum = MainApp.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    if (url.getPath().contains("gui")) {
                        InputStream is = url.openStream();
                        if (is != null) {
                            Manifest manifest = new Manifest(is);
                            Attributes mainAttribs = manifest.getMainAttributes();
                            String version = mainAttribs.getValue("Implementation-Version");
                            if (version != null) {
                                return "version " + version;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
        } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
        }
        return "";
    }
}
