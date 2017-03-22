package core;

import core.Core;
import core.CoreEngine;
import core.configuration.JStesConfiguration;
import main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore implements CoreListener{

    private final static Core coreEngine = CoreEngine.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public JStesCore() {

        coreEngine.addCoreEventListener(this);
        //read configuration
        try {

            File confFile = new File(Main.class.getClassLoader().getResource("configuration/configuration.xml").getFile());
            JStesConfiguration jStesConfiguration = new JStesConfiguration();
            jStesConfiguration.loadConfiguration(confFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            logger.error("Cannot find configuration file");
        }

    }

    /**
     * Get core engine
     * @return coreEngine
     */
    public static Core getCoreEngine() {
        return coreEngine;
    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getAction() == CoreEventType.SSH_CONNECTION_ERROR) {
            logger.error("Ssh connection");
        }
    }
}
