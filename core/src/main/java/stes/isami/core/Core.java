package stes.isami.core;

import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;
import stes.isami.core.ssh.SshFactory;

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
    public boolean addJob(Job job) throws JobException;

    /**
     * Delete a list of jobs
     * @param uuidList
     */
    public void deleteJobs(List<UUID> uuidList);

    /**
     * Stop job
     * @param id
     */
    public void stopJob(UUID id);

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
    public void executeJob(UUID id) throws IllegalStateException;

    /**
     * Execute all the jobs
     */
    public void executeAll();

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
     * Add job listener
     * @param listener
     */
    public void addJobListener(JobListener listener);

    /**
     * Remove job listener
     * @param listener
     */
    public void removeJobListener(JobListener listener);

    /**
     * Close the executors
     */
    public void shutdown();

}
