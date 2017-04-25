package core.job;

import core.modules.MethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;

import java.util.List;
import java.util.Observer;
import java.util.UUID;

/**
 * Interface for the job class. Each job class has to implement this interface.
 */
public interface Job extends Executable {

    /**
     * Get the name of the job
     *
     * @return name of the job
     */
    public String getName();

    /**
     * Get the ID
     *
     * @return the ID
     */
    public UUID getID();

    /**
     * Returns if the job is editable or not
     *
     * @return true if the job is editable. otherwise if not
     */
    public boolean isEditable();

    /**
     * Get the parameter set of the job
     *
     * @return clone of the parameter set
     */
    public ParameterSet getParameters();

    /**
     * Delete the job.
     * If the job is running, it tries to delete itself from the batch system. If it succeed, it change the
     * state to DELETED.
     * <br> In case of batch error, it throws a {@code JobExceltion}
     * <pre>If the job is not running, it changes the state immediately to DELETED</pre>
     * @return
     */
    public void delete() throws JobException;

    /**
     * Stop the job
     */
    public void stop();

    /**
     * Restart the job
     */
    public void restart();
    /**
     * Update a pameter
     *
     * @param newParameter
     * @throws JobException if the job is editable
     */
    public void updateParameter(Parameter<?> newParameter) throws JobException;

    /**
     * Update a parameter
     * @param parameterName name of the parameter to be updated
     * @param parameterValue new value
     * @throws IllegalArgumentException if the parameter is not found or it cannot be updated
     */
    public void updateParameter(String parameterName,Object parameterValue) throws IllegalArgumentException;

    /**
     * Update the parameter set
     *
     * @param parameters
     * @throws JobException if the job is editable
     */
    public void updateParametes(ParameterSet parameters) throws JobException;


    /**
     * Get the current status of the job
     *
     * @return current status of the job
     */
    public int getState();

    /**
     * Set the output from the qstat command
     * @param qstatOutput
     */
    public void setQstatResult(MethodResult qstatOutput);

    /**
     * Add observer
     * @param observer
     */
    public void addObserver(Observer observer);

}
