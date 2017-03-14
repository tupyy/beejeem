package core.job;

import java.util.concurrent.Executor;

/**
 * Interface to be implemented by any job or scenario which can be executed.
 */
public interface Executable {

    /**
     * Execute job or scenario
     * @param progress The {@code JobExecutionProgress} is a interface which can be implemented by the caller
     *                 in order to keep track of the execution progress of the job
     */
    public void execute(JobExecutionProgress progress) throws JobException;
}
