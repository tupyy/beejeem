package stes.isami.core.job;

/**
 * Interface to be implemented by any job or scenario which can be executed.
 */
public interface Executable {

    /**
     * Execute job or scenario
     */
    public void execute() throws JobException;
}
