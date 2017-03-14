package core.modules;

import core.parameters.ParameterSet;

import java.util.List;
import java.util.UUID;

/**
 * It implements the result of a methods.
 */
public interface MethodResult {

    public static final int OK = 0;

    public static final int ERROR = 1;

    /**
     * Get the methods name
     * @return
     */
    public String getMethodName();

    /**
     * Get the module name
     * @return
     */
    public String getModuleName();

    /**
     * Return the exit code of the methods
     * @return 0 OK, 1 KO
     */
    public  int getExitCode();

    /**
     * Set the exit code
     */
    public void setExitCode(int exitCode);

    /**
     * Return a list of the error messages
     * @return list of error message
     */
    public List<String> getErrorMessages();

    /**
     * Add a new error message
     * @param message
     */
    public void addErrorMessage(String message);

    /**
     * Get the ID of the job for which the methods was executed
     * @return job UUID
     */
    public UUID getJobID();

    /**
     * The methods can either create or modify some of the job parameters.
     * This methods returns a parameter set with the new/modified parameters
     * @return parameter set
     */
    public ParameterSet getResultParameters();

    /**
     * Indicate that the methods was the last one from the module
     * @return
     */
    public Boolean isLast();

    /**
     * Get class name
     * @return class name
     */
    public Class<? extends MethodResult> getMethodResultClass();

}
