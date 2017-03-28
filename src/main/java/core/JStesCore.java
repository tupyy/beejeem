package core;

import com.sshtools.ssh.SshException;
import core.configuration.JStesConfiguration;
import core.configuration.JStesPreferences;
import core.parameters.Parameter;
import core.ssh.SshListener;
import javafx.scene.control.Alert;
import main.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore implements CoreListener,SshListener{

    private final static Core coreEngine = CoreEngine.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public JStesCore() {

        coreEngine.addCoreEventListener(this);
        //read configuration
        try {

            File confFile = new File(MainApp.class.getClassLoader().getResource("configuration/configuration.xml").getFile());
            JStesConfiguration jStesConfiguration = new JStesConfiguration();
            jStesConfiguration.loadConfiguration(confFile);



        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            logger.error("Cannot find configuration file");
        }

        getCoreEngine().getSshFactory().addSshEventListener(this);
        JStesPreferences preferences = JStesConfiguration.getPreferences();


        try {
            String username = (String) preferences.getUserConfiguration().getParameter("username").getValue();
            String host = (String) preferences.getUserConfiguration().getParameter("host").getValue();
            String password = (String) preferences.getUserConfiguration().getParameter("password").getValue();

            if (username.isEmpty() || host.isEmpty() || password.isEmpty()) {
                logger.error("Cannot connect to remote host. Username or host or password is missing");
            }
            else {
                getCoreEngine().getSshFactory().connect(host, username, password);
            }
        }
        catch (IllegalArgumentException ex) {
            logger.error("Cannot connect to remote host: {}",ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR,"Connect to remote host. ".concat(ex.getMessage()));
            alert.setContentText("Cannot connect to remote host");
            alert.show();

        }   catch (SshException e) {
            logger.error("Cannot connect to remote host: {}",e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR,"Connect to remote host");
            alert.setContentText("Cannot connect to remote host. ".concat(e.getMessage()));
            alert.show();
        }


    }

    /**
     * Get core engine
     * @return coreEngine
     */
    public static Core getCoreEngine() {
        return coreEngine;
    }

    public void shutdown() {
        getCoreEngine().shutdown();
        getCoreEngine().getSshFactory().disconnect();
    }

    @Override
    public void coreEvent(CoreEvent e) {
        if (e.getAction() == CoreEventType.SSH_CONNECTION_ERROR) {
            logger.error("Ssh connection");
        }
    }

    @Override
    public void channelClosed() {

    }

    @Override
    public void channelClosing() {

    }

    @Override
    public void connected() {
        logger.info("SSH client connected");
    }

    @Override
    public void authenticated() {
        logger.info("SSH client authenticated");
    }
}
