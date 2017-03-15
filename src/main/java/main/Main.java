package main;

import javafx.application.Application;
import gui.MainApp;
import main.configuration.JStesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * StesJobLauncher Main class
 */
public class Main {


    private static final Logger logger = LoggerFactory
            .getLogger(Main.class);


    public static void main(String[] args) {


        //read configuration
        try {

            File confFile = new File(Main.class.getClassLoader().getResource("configuration/configuration.xml").getFile());
            JStesConfiguration  jStesConfiguration = new JStesConfiguration();
            jStesConfiguration.loadConfiguration(confFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            logger.error("Cannot find configuration file");
        }

        //start JStesCore
        JStesCore stesJLCore = new JStesCore();

        //launch app
        Application.launch(MainApp.class,args);
    }

}
