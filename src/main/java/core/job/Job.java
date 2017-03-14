package core.job;

import core.parameters.Parameter;
import core.parameters.ParameterSet;

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
     * Get the parameter
     *
     * @param parameterName
     * @return the clone of the parameter parameterName
     */
    public Parameter<?> getParameter(String parameterName);

    /**
     * Get the parameter set of the job
     *
     * @return clone of the parameter set
     */
    public ParameterSet getParameters();

    /**
     * A running job has to be marked for deletion before deleting it. Otherwise the job cannot be deleted.
     */
    public void markForDeletion();

    /**
     * Return true if the job is marked of deletion.
     */
    public boolean isMarkedForDeletion();

    /**
     * Return a {@link JobRecord} object containing informations about the job
     * @return {@link JobRecord} object
     */
    public JobRecord collectData();

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
    public int getStatus();

}
