package core.job;

import java.util.concurrent.Executor;

/**
 * Interface to be implemented by any job or scenario which can be executed.
 */
public interface Executable {

    /**
     * Execute job or scenario
     */
    public void execute() throws JobException;
}
