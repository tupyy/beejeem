package main;

import com.google.common.eventbus.EventBus;
import core.*;
import core.job.JobException;
import core.ssh.SshListener;
import eventbus.*;
import eventbus.CoreEvent;
import eventbus.JobEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore implements JobListener,SshListener,ComponentEventHandler{

    private final static Core coreEngine = CoreEngine.getInstance();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final EventBus eventBus = new EventBus();

    public JStesCore() {

        getCoreEngine().getSshFactory().addSshEventListener(this);
        getCoreEngine().addJobListener(this);
        eventBus.register(this);

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
        getCoreEngine().shutdown();
        getCoreEngine().getSshFactory().disconnect();
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
        eventBus.post(new DefaultCoreEvent(CoreEvent.CoreEventType.SSH_CLIENT_AUTHENTICATED));
    }

    @Override
    public void authenticated() {
        logger.info("SSH client authenticated");
    }
    //</editor-fold>

    //<editor-fold desc="JobListener">
    @Override
    public void jobCreated(UUID id) {
        eventBus.post(new DefaultJobEvent(JobEvent.JobEventType.JOB_CREATED,id));
    }

    @Override
    public void jobUpdated(UUID id) {
        eventBus.post(new DefaultJobEvent(JobEvent.JobEventType.JOB_UPDATED,id));
    }

    @Override
    public void jobDeleted(UUID ids) {

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
            case DELETE:
                try {
                    getCoreEngine().deleteJob(event.getJobId());
                } catch (JobException e) {
                    e.printStackTrace();
                }
        }

    }

    @Override
    public void onCoreEvent(CoreEvent event) {

    }
    //</editor-fold>
}
