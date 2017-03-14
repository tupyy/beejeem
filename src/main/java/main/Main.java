package main;

import javafx.application.Application;
import gui.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StesJobLauncher Main class
 */
public class Main {

    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);


    public static void main(String[] args) {

        JStesCore stesJLCore = new JStesCore();

        Application.launch(MainApp.class,args);
    }

}
