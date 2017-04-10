package main;

import core.*;
import core.ssh.SshListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore implements CoreListener,SshListener{

    private final static Core coreEngine = CoreEngine.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public JStesCore() {

        coreEngine.addCoreEventListener(this);
        getCoreEngine().getSshFactory().addSshEventListener(this);
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
