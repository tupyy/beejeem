package core;

import core.creator.CreatorFactory;
import core.job.Job;
import core.job.JobException;
import core.job.JobExecutionProgress;
import core.job.ModuleController;
import core.modules.ModuleStarter;
import core.parameters.ParameterSet;
import core.plugin.Plugin;
import core.plugin.PluginLoader;
import core.ssh.SshFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core interface
 */

public interface Core {

    /**
     * Load plugin from folder {@code pluginPath}
     * @param pluginPath
     * @throws IOException of pluginPath not found
     */
    public void loadPlugins(String pluginPath) throws IOException;

    /**
     * Add a new job
     * @param job
     */
    public void addJob(Job job) throws JobException;

    /**
     * Delete the job. A job which is running cannot be deleted immediately.
     * <br>The job is marked for deletion and a QDel module is executed with the id of the job.
     * <br>Once the job have been deleted from the batch system, the job can be safely deleted from jobList.
     * @param id
     * @throws JobException
     */
    public void deleteJob(UUID id) throws JobException;

    /**
     * Get the job
     * @param id
     * @return Job
     */
    public Job getJob(UUID id);

    /**
     * Run job
     * @param id
     */
    public void executeJob(UUID id, JobExecutionProgress progress);

    /**
     * Return the SshFactory
     * @return
     */

    public SshFactory getSshFactory();
    /**
     * Count the number of jobs
     * @return
     */
    public int count();

    /**
     * Get the list of ids of all jobs
     * @return ArrayList of IDs
     */
    public ArrayList<UUID> getJobIDList();

    /**
     * Add CoreListener
     * @param l CoreListener interface
     */
    public void addCoreEventListener(CoreListener l);

    /**
     * Remove listener
     * @param l
     */
    public void removeCoreEventListener(CoreListener l);

    /**
     * Close the executors
     */
    public void shutdown();

}
