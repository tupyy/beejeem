package main;

import core.JStesCore;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StesJobLauncher Main class
 */
public class Main {


    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);


    public static void main(String[] args) {

        //start JStesCore
        JStesCore stesJLCore = new JStesCore();

        //launch app
        Application.launch(MainApp.class,args);
    }

}
