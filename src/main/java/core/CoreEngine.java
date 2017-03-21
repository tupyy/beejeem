package core;

import core.job.*;
import core.modules.ModuleStarter;
import core.parameters.ParameterSet;
import core.ssh.SshFactory;
import core.ssh.SshRemoteFactory;
import core.tasks.ModuleExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class implements the Core interface. It represents the main class of the app.
 * <br> It extends Observable to notify the observer when a job
 */
public final class CoreEngine extends Observable implements Core, Observer {


    private final QStatManager qstatManager;
    private final ModuleStarter moduleStarter;

    private transient Vector listeners;
    private SshRemoteFactory sshRemoteFactory;

    private final Logger logger = LoggerFactory.getLogger(CoreEngine.class);

    private final ModuleExecutor executor;
    List<Job> jobList = new ArrayList<>();

    //temp
    private int finishedJobs = 0;

    private static Core coreInstance = null;

    /**
     * Private constructor to restrict to only one instance of this class
     */
    private CoreEngine() {

        CoreLogging.configureLogging();
        executor = new ModuleExecutor();
        sshRemoteFactory = new SshRemoteFactory();
        this.qstatManager = new QStatManager(this,executor);

        //init the states
        JobState jobState = new JobState();

        /**
         * Load modules on a new thread after the GUI has started
         */
        moduleStarter = new ModuleStarter();
        Thread readModuleThread = new Thread(moduleStarter);
        readModuleThread.start();
    }

    /**
     * Get the instance of CoreEngine
     * @return instance of coreEngine
     */
    public static Core getInstance() {
        if (coreInstance == null) {
            coreInstance = new CoreEngine();
        }

        return coreInstance;
    }

    //<editor-fold desc="CORE INTERFACE">
    @Override
    public void createJob(ParameterSet parameterSet,List<ModuleController> modules) throws JobException {

        String jobName = parameterSet.getParameter("name").getValue().toString();
        SimpleJob job = new SimpleJob(parameterSet,modules);
        jobList.add(job);
        job.addObserver(this);

        fireCoreEvent(CoreEventType.JOB_CREATED, job.getID());
        logger.info("Job created: {}",job.getName());

    }

    @Override
    public void addJob(Job j) throws JobException{
        if (jobExists(j.getID())) {
            throw new JobException(JobException.JOB_EXISTS,"Job ".concat(j.getID().toString()).concat(" already exists"));
        }

        jobList.add(j);

        AbstractJob aj = (AbstractJob) j;
        aj.addObserver(this);

        fireCoreEvent(CoreEventType.JOB_CREATED, j.getID());
        logger.info("Job created: {}",j.getName());
    }

    @Override
    public void deleteJob(UUID id) throws JobException {

    }

    @Override
    public Job getJob(UUID id) {
        for (Job j : jobList) {
            if (j.getID().equals(id)) {
                return j;
            }
        }
        return null;
    }

    @Override
    public void executeJob(UUID id,JobExecutionProgress progress) {

        //check if the ssh client is connected before executing jobs
        if (sshRemoteFactory.isConnected() && sshRemoteFactory.isAuthenticated()) {
            qstatManager.start();
            Job job = getJob(id);
            try {
                job.execute(progress);
            } catch (JobException e) {
                e.printStackTrace();
            }
        }
        else {
            fireCoreEvent(CoreEventType.SSH_CONNECTION_ERROR,UUID.randomUUID());
        }
    }

    @Override
    public void runAll() {
        for (Job job : jobList) {
            executeJob(job.getID(),null);
        }
    }

    @Override
    public SshFactory getSshFactory() {
        return sshRemoteFactory;
    }

    @Override
    public int count() {
        return jobList.size();
    }

    @Override
    public void update(Observable o, Object arg) {
        SimpleJob j = (SimpleJob) o;
        fireCoreEvent(CoreEventType.JOB_STATUS_CHANGED, j.getID());

        if (j.getStatus() == JobState.FINISHED) {
            finishedJobs++;
            logger.info("Finished jobs: {}", finishedJobs);
        }
    }

    @Override
    public ArrayList<UUID> getJobIDList() {
        ArrayList<UUID> idList = new ArrayList<>();

        for (Job job: jobList) {
            idList.add(job.getID());
        }

        return idList;
    }

    /**
     * Register a listener for JobEvents
     */
    @Override
    synchronized public void addCoreEventListener(CoreListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }

    /**
     * Remove a listener for JobEvents
     */
    @Override
    synchronized public void removeCoreEventListener(CoreListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.removeElement(l);
    }

    @Override
    public ModuleStarter getModuleStarter() {
        return moduleStarter;
    }


    //</editor-fold>

    public SimpleJob getStandardJob(UUID id) {
        return (SimpleJob) getJob(id);
    }

    /**
     * Return true if the jobID exists
     *
     * @param jobID
     * @return
     */
    public boolean jobExists(UUID jobID) {
        for (Job job : jobList) {
            if (job.getID().equals(jobID)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Fire JobEvent to all registered listeners
     */
    protected void fireCoreEvent(CoreEventType action, UUID id) {
        // if we have no listeners, do nothing...
        if (listeners != null && !listeners.isEmpty()) {
            // create the event object to send
            CoreEvent event =
                    new CoreEvent(this, action, id);

            // make a copy of the listener list in case
            //   anyone adds/removes listeners
            Vector targets;
            synchronized (this) {
                targets = (Vector) listeners.clone();
            }

            // walk through the listener list and
            //   call the sunMoved methods in each
            Enumeration e = targets.elements();
            while (e.hasMoreElements()) {
                CoreListener l = (CoreListener) e.nextElement();
                l.coreEvent(event);
            }
        }
    }



}
