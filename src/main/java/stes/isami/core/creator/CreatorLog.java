package stes.isami.core.creator;

/**
 * Created by tctupangiu on 21/03/2017.
 */
public interface CreatorLog {

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
