package main;

import jobexcore.StesJLCore;
import javafx.application.Application;
import mainview.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StesJobLauncher Main class
 */
public class Main {

    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);



    public static void main(String[] args) {

        StesJLCore stesJLCore = new StesJLCore();

        Application.launch(MainApp.class,args);
    }

}
