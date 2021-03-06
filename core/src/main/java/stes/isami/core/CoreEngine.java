package stes.isami.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import stes.isami.core.creator.CreatorFactory;
import stes.isami.core.job.*;
import stes.isami.core.plugin.PluginLoader;
import stes.isami.core.ssh.SshFactory;
import stes.isami.core.ssh.SshListener;
import stes.isami.core.ssh.SshRemoteFactory;
import stes.isami.core.tasks.ModuleExecutor;
import stes.isami.core.util.TmpFileCleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class implements the Core interface. It represents the stes.isami.main class of the app.
 * <br> It extends Observable to notify the observer when a job
 */
public final class CoreEngine extends AbstractCoreEngine implements Core,SshListener {


    private final QStatManager qstatManager;

    private CreatorFactory creatorFactory = new CreatorFactory();

    private SshRemoteFactory sshRemoteFactory;

    private final Logger logger = LoggerFactory.getLogger(CoreEngine.class);

    private final ModuleExecutor executor;

    private EventBus coreEventBus = new EventBus();

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
        sshRemoteFactory.addSshEventListener(this);
        this.qstatManager = new QStatManager(this,executor);

        //init the states
        JobState jobState = new JobState();
        coreEventBus.register(this);
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
    public boolean addJob(Job j) throws JobException {
        if (jobExists(j.getID())) {
            throw new JobException(JobException.JOB_EXISTS,"Job ".concat(j.getID().toString()).concat(" already exists"));
        }

        j.setEventBus(getCoreEventBus());
        jobList.put(j,false);

        logger.info("Job created: {}",j.getName());
        fireJobEvent(new JobEvent(j.getID(), JobEvent.JobEventType.CREATE));

        return true;
    }

    @Override
    public void deleteJobs(List<UUID> uuidList) {
       List<UUID> deletedJobList = new ArrayList<>();
       for(UUID id: uuidList) {
            if (deleteJob(id)) {
                deletedJobList.add(id);
            }
        }
        fireJobEvent(new JobEvent(deletedJobList, JobEvent.JobEventType.DELETE));
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
    public void executeJob(UUID id) throws IllegalStateException {

//        //check if the ssh client is connected before executing jobs
        if ( !sshRemoteFactory.isConnected() && !sshRemoteFactory.isAuthenticated()) {
            throw new IllegalStateException("SSH disconnected");
        }

        qstatManager.start();
        Job job = getJob(id);
        try {
            if (job.getState() == JobState.READY) {
                job.execute();
            }
            else if (job.getState() == JobState.STOP || job.getState() == JobState.ERROR || job.getState() == JobState.FINISHED) {
                job.restart();
            }
        } catch (JobException e) {
            e.printStackTrace();
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
        executor.shutDownExecutor();
    }

    /**
     * Return the event bus used by the job to send events
     * @return
     */
    public EventBus getCoreEventBus() {
        return coreEventBus;
    }

    @Subscribe
    public void onJobStateChanged(JobStateChangedEvent event) {

        Job j = getJob(event.getId());
        switch (j.getState()) {
            case JobState.STOP:
                try {
                    if (isMarkedForDeletion(j)) {
                        logger.info("Job {} stopped. It is marked for deletion",j.getName());
                        jobList.remove(j);
                        fireJobEvent(new JobEvent(j.getID(), JobEvent.JobEventType.DELETE));
                        return;
                    }
                } catch (IllegalArgumentException ex) {
                    ;
                }
        }

        fireJobEvent(event);

    }

    @Subscribe
    public void onJobUpdate(JobEvent event) {
        fireJobEvent(event);
    }

    /***********************************************************************************
     *
     *
     *                                      SSH LISTENER
     *
     ***********************************************************************************/

    @Override
    public void channelClosed() {
        disconnected();
    }

    @Override
    public void channelClosing() {

    }

    @Override
    public void connected() {

    }

    @Override
    public void authenticated() {

    }

    @Override
    public void disconnected() {
        qstatManager.stop();

        for (UUID id: getJobIDList()) {
            Job job = getJob(id);
            if (isJobRunning(job)) {
                job.stop();
            }
        }
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


    /***********************************************************************************
     *
     *
     *                                      PRIVATE
     *
     ***********************************************************************************/

    /**
     * Return true if the job {@code j} is running
     * @param j
     * @return
     */
    private boolean isJobRunning(Job j) {
        int state = j.getState();

        if (state == JobState.READY ||
                state == JobState.STOP ||
                state == JobState.ERROR
                || state == JobState.FINISHED) {
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
     * Delete job. return true if job deleted.
     * A running job will be marked as deleted and stopped
     * @param id
     * @return
     */
    private boolean deleteJob(UUID id) {

        Job j = getJob(id);
        if (isJobRunning(j)) {
            try {
                markJobForDeletion(j);
                j.stop();
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        else {
            logger.info("Delete job {}", j.getID());
            jobList.remove(j);
        }
        return true;
    }

}
