package core;

import core.creator.CreatorFactory;
import core.garbage.GarbageCollector;
import core.job.*;
import core.plugin.PluginLoader;
import core.ssh.SshFactory;
import core.ssh.SshRemoteFactory;
import core.tasks.ModuleExecutor;
import core.util.TmpFileCleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class implements the Core interface. It represents the main class of the app.
 * <br> It extends Observable to notify the observer when a job
 */
public final class CoreEngine extends AbstractCoreEngine implements Core, Observer {


    private final QStatManager qstatManager;
    private final GarbageCollector garbageCollector;

    private CreatorFactory creatorFactory = new CreatorFactory();

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

        TmpFileCleanup cleanup = new TmpFileCleanup();
        Thread tmpCleanupThread = new Thread(cleanup);
        tmpCleanupThread.setPriority(Thread.MIN_PRIORITY);
        tmpCleanupThread.start();

        executor = new ModuleExecutor();
        sshRemoteFactory = new SshRemoteFactory();
        this.qstatManager = new QStatManager(this,executor);

        garbageCollector = new GarbageCollector(this);

        //init the states
        JobState jobState = new JobState();
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
    public void loadPlugins(String pluginPath) throws IOException {
        File file = new File(pluginPath);
        if ( !file.isDirectory() ) {
            throw new IOException("Plugin path not found");
        }

        PluginLoader pluginLoader = new PluginLoader(pluginPath);
        CompletableFuture completableFuture = CompletableFuture.supplyAsync(pluginLoader).thenApply(result -> {
            creatorFactory.loadCreators(pluginLoader);
            return null;
        });
        completableFuture.exceptionally( th -> {
            logger.error("Load plugins: {}" ,th.toString());
            return null;
        });
    }

    @Override
    public void addJob(Job j) throws JobException{
        if (jobExists(j.getID())) {
            throw new JobException(JobException.JOB_EXISTS,"Job ".concat(j.getID().toString()).concat(" already exists"));
        }

        j.addObserver(this);
        jobList.add(j);

        fireJobEvent(JobEvent.JOB_CREATED, j.getID());
        logger.info("Job created: {}",j.getName());
    }

    @Override
    public void deleteJob(UUID id) throws JobException {
        Job j = getJob(id);
        j.delete();
    }

    @Override
    public void deleteJobs(List<UUID> ids) throws JobException {

        //TODO create a thread here
        for (UUID id: ids) {
            try {
                Job j = getJob(id);
                getGarbageCollector().registerJobForDeletion(j.getID(),(String) j.getParameters().getParameter("batchID").getValue());
            }
            catch (IllegalArgumentException ex) {
                ;
            }
            finally {
                getJob(id).delete();
            }
        }
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
    public void executeJob(UUID id) {

//        //check if the ssh client is connected before executing jobs
        if (sshRemoteFactory.isConnected() && sshRemoteFactory.isAuthenticated()) {
            qstatManager.start();
            Job job = getJob(id);
            try {
                if (job.getState() == JobState.READY) {
                    job.execute();
                }
                else if (job.getState() == JobState.STOP) {
                    job.restart();
                }
            } catch (JobException e) {
                e.printStackTrace();
            }
        }
        else {
            fireCoreEvent(CoreEvent.SSH_CONNECTION_ERROR);
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
        Job j = (Job) o;

        switch (j.getState()) {

            case JobState.FINISHED:
                finishedJobs++;
                logger.info("Finished jobs: {}", finishedJobs);
                fireJobEvent(JobEvent.JOB_UPDATED, j.getID());
                break;

            case JobState.STOP:
                try {
                    garbageCollector.registerJobForDeletion(j.getID(),(String) j.getParameters().getParameter("batchID").getValue());
                }
                catch (IllegalArgumentException ex) {
                    ;
                }
            default:
                fireJobEvent(JobEvent.JOB_UPDATED, j.getID());
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

    @Override
    public void shutdown() {
        garbageCollector.shutdown();
        executor.shutDownExecutor();
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

    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }




}
