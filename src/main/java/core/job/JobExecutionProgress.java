package core.job;

/**
 * Interface for monitoring the state of a job execution.
 */
public interface JobExecutionProgress {

    /**
     * Fatal error means that the operation has been aborted.
     * @param message
     */
    public void fatalError(String message);

    /**
     * Add a new error message
     * @param message
     */
    public void error(String message);

    /**
     * Add warning message
     * @param message
     */
    public void warning(String message);

    /**
     * add debug message
     * @param message
     */
    public void debug(String message);

    /**
     * Add sidepanel message
     * @param message
     */
    public void info(String message);
}
