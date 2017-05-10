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

    /**
     * Holds the jobs. The {@code Boolean} is for marking the job as deleted.
     * When a job is deleted, it is marked as deleted and it is triggered the STOP trigger.
     * After the job has stopped, the job is safely deleted
     */
    Map<Job,Boolean> jobList = new HashMap<>();

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
    public boolean addJob(Job j) throws JobException{
        if (jobExists(j.getID())) {
            throw new JobException(JobException.JOB_EXISTS,"Job ".concat(j.getID().toString()).concat(" already exists"));
        }

        j.addObserver(this);
        jobList.put(j,false);

        logger.info("Job created: {}",j.getName());
        return true;
    }

    @Override
    public boolean deleteJob(UUID id) {

        Job j = getJob(id);
        if (isJobRunning(j)) {
            try {
                markJobForDeletion(j);
                j.stop();
            } catch (IllegalArgumentException ex) {
               return false;
            }
        } else {
            logger.info("Delete job {}", j.getID());
            deleteJobInternally(j);
        }

        return true;
    }

    @Override
    public void stopJob(UUID id) {
        Job j = getJob(id);

        if (j != null) {
            j.stop();
        }
    }

    @Override
    public Job getJob(UUID id) {
        for (Map.Entry<Job,Boolean> entry: jobList.entrySet()) {
            if ( !entry.getValue() ) {
                if (entry.getKey().getID().equals(id)) {
                    return entry.getKey();
                }
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
                else if (job.getState() == JobState.STOP || job.getState() == JobState.ERROR) {
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
    public void executeAll() {

        for (Map.Entry<Job,Boolean> entry: jobList.entrySet()) {
            if ( !entry.getValue() ) {
                executeJob(entry.getKey().getID());
            }
        }
    }

    @Override
    public SshFactory getSshFactory() {
        return sshRemoteFactory;
    }

    @Override
    public int count() {
        int countJob = 0;
        for (Map.Entry<Job,Boolean> entry: jobList.entrySet()) {
            if ( !entry.getValue() ) {
                countJob++;
            }
        }

        return countJob;
    }

    @Override
    public void update(Observable o, Object arg) {
        Job j = (Job) o;

             switch (j.getState()) {

                case JobState.FINISHED:
                    finishedJobs++;
                    logger.info("Finished jobs: {}", finishedJobs);
                    break;

                case JobState.STOP:
                    try {
//                        garbageCollector.registerJobForDeletion(j.getID(), (String) j.getParameters().getParameter("batchID").getValue());
                        if (isMarkedForDeletion(j)) {
                            logger.info("Job {} stopped. It is marked for deletion");
                            deleteJobInternally(j);
                            return;
                        }
                    } catch (IllegalArgumentException ex) {
                        ;
                    }
            }

        fireJobEvent(JobEvent.JOB_UPDATED, j.getID());

    }

    @Override
    public ArrayList<UUID> getJobIDList() {
        ArrayList<UUID> idList = new ArrayList<>();

        for (Map.Entry<Job,Boolean> entry: jobList.entrySet()) {
            if ( !entry.getValue()) {
                idList.add(entry.getKey().getID());
            }
        }

        return idList;
    }

    @Override
    public void shutdown() {
        qstatManager.stop();
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
        Job j = getJob(jobID);

        if (j != null) {
            return true;
        }

        return false;
    }

    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    private boolean isJobRunning(Job j) {
        int state = j.getState();

        if (state == JobState.READY ||
                state == JobState.STOP ||
                state == JobState.ERROR) {
            return false;
        }

        return true;
    }

    /**
     * Return true if the job has been marked for deletion
     * @param job
     * @return
     */
    private boolean isMarkedForDeletion(Job job) {

        if (jobList.containsKey(job)) {
            return jobList.get(job);
        }

        return false;
    }

    private void markJobForDeletion(Job job) {

        if (jobList.containsKey(job)) {
            logger.info("Marking job {} for deletion",job.getID());
            jobList.put(job,true);
        }

    }

    /**
     * Get the job even is is marked for deletion
     * @param id
     * @return
     */
    private Job getJobInternally(UUID id) {
        ArrayList<UUID> idList = new ArrayList<>();

        for (Map.Entry<Job,Boolean> entry: jobList.entrySet()) {
            if (entry.getKey().equals(id)) {
               return entry.getKey();
            }
        }

        return null;
    }

    private void deleteJobInternally(Job j) {
        jobList.remove(j);
    }


}
