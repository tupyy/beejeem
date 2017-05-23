package main;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import core.*;
import core.ssh.SshListener;
import eventbus.*;
import eventbus.CoreEvent;
import eventbus.JobEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import stes.isami.tcpserver.TcpEvent;
import stes.isami.tcpserver.TcpServer;
import stes.isami.tcpserver.TcpServerImpl;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore implements JobListener,SshListener,ComponentEventHandler {

    private final static Core coreEngine = CoreEngine.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final TcpServer tcpServer;

    private LinkedBlockingDeque<Element> tcpClientOutputQueue = new LinkedBlockingDeque<>();

    private static EventBus eventBus;

    public JStesCore() {

        getCoreEngine().getSshFactory().addSshEventListener(this);
        getCoreEngine().addJobListener(this);

        eventBus = new EventBus();
        eventBus.register(this);

        tcpServer = new TcpServerImpl(eventBus);
        tcpServer.start(1000,tcpClientOutputQueue);

    }

    /**
     * Get core engine
     * @return coreEngine
     */
    public static Core getCoreEngine() {
        return coreEngine;
    }

    /**
     * Register the componentController to the {@code eventBus}
     * @param componentController
     */
    public static void registerController(ComponentEventHandler componentController) {
        eventBus.register(componentController);
    }

    /**
     * Get the event bus
     * @return
     */
    public static EventBus getEventBus() {
        return eventBus;
    }

    public void shutdown() {
        eventBus.post(new DefaultCoreEvent(CoreEvent.CoreEventType.SHUTDOWN));
        getCoreEngine().shutdown();
        getCoreEngine().getSshFactory().disconnect();
        tcpServer.stop();
    }

    //<editor-fold desc="SSH Listener">
    @Override
    public void channelClosed() {
        eventBus.post(new DefaultCoreEvent(CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED));
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
        eventBus.post(new DefaultCoreEvent(CoreEvent.CoreEventType.SSH_CLIENT_AUTHENTICATED));
    }

    @Override
    public void disconnected() {
        logger.info("SSH client authenticated");
        eventBus.post(new DefaultCoreEvent(CoreEvent.CoreEventType.SSH_CLIENT_DISCONNECTED));
    }
    //</editor-fold>

    //<editor-fold desc="JobListener">

    @Override
    public void jobUpdated(UUID id) {
        eventBus.post(new DefaultJobEvent(JobEvent.JobEventType.JOB_UPDATED,id));
    }

   //</editor-fold>

    //<editor-fold desc="ComponentEventHandler">
    @Override
    public void onJobEvent(JobEvent event) {

    }

    @Override
    public void onComponentAction(ComponentAction event) {

        switch (event.getAction()) {
            case EXECUTE:
                getCoreEngine().executeJob(event.getJobId());
                break;
            case EXECUTE_ALL:
                getCoreEngine().executeAll();
                break;
            case STOP:
                getCoreEngine().stopJob(event.getJobId());
                break;
        }

    }

    @Override
    public void onCoreEvent(CoreEvent event) {

    }

    //</editor-fold>
}
