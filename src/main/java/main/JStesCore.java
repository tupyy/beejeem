package main;

import com.google.common.eventbus.EventBus;
import configuration.JStesConfiguration;
import configuration.JStesPreferences;
import core.Core;
import core.CoreEngine;
import core.JobListener;
import core.job.Job;
import core.job.JobException;
import core.ssh.SshListener;
import eventbus.*;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import stes.isami.tcpserver.ClientMessage;
import stes.isami.tcpserver.TcpServer;
import stes.isami.tcpserver.TcpServerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore implements JobListener,SshListener,ComponentEventHandler {

    private final static Core coreEngine = CoreEngine.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final TcpServer tcpServer;
    private Thread readerThread;

    private LinkedBlockingDeque<ClientMessage> tcpClientOutputQueue = new LinkedBlockingDeque<>();

    private static EventBus eventBus;

    public JStesCore() {

        getCoreEngine().getSshFactory().addSshEventListener(this);
        getCoreEngine().addJobListener(this);

        eventBus = new EventBus();
        eventBus.register(this);

        /**
         * Start tcp server
         */
        tcpServer = TcpServerImpl.getInstance(eventBus);
        tcpServer.start(1000,tcpClientOutputQueue);

        readerThread = new Thread(new TcpMessageReader(tcpClientOutputQueue));
        readerThread.start();


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
        readerThread.interrupt();
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

    /**
     * Method called when a new message from the tcpClient arrived.
     * @param tcpClientMessage
     */
    public void onTcpMessage(ClientMessage tcpClientMessage) {
        switch (tcpClientMessage.getType()) {
            case ClientMessage.PAYLOAD_MESSAGE:
                List<String> errorMessage = new ArrayList<>();

                TcpJobCreator tcpJobCreator = new TcpJobCreator();
                List<Job> createdJobs =  tcpJobCreator.createJobs((Element) tcpClientMessage.getPayload(),errorMessage);
                for(Job job: createdJobs) {
                    try {
                        if (getCoreEngine().addJob(job)) {

                            Platform.runLater(() -> {
                                JStesCore.getEventBus().post(new DefaultJobEvent(JobEvent.JobEventType.JOB_CREATED, job.getID()));

                                //check if autoRun is true
                                if (JStesConfiguration.getPreferences().getProperty("autoJobRun").getValue() == Boolean.TRUE) {
                                    getCoreEngine().executeJob(job.getID());
                                }
                            });

                        }
                    } catch (JobException e) {
                        logger.error(e.getMessage());
                    }
                }
                break;
            case ClientMessage.ERROR_MESSAGE:
                logger.info("TcpClient error message: {}",tcpClientMessage.getErrorMessage());
                break;
        }

    }

    /**
     * Class to read from the inputQueue
     */
    private class TcpMessageReader implements Runnable {

        private final BlockingDeque<ClientMessage> inputQueue;

        public TcpMessageReader(BlockingDeque<ClientMessage> inputQueue) {
            this.inputQueue = inputQueue;
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    ClientMessage clientMessage = inputQueue.take();
                    onTcpMessage(clientMessage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("TcpMessageReader interrupted");
                }
            }
        }
    }
}
