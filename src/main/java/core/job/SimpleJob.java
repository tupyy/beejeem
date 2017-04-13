package core.job;

import core.modules.MethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Observable;

/**
 * Basic implementation for a job. It can be a generic or spectre job.
 * In the v2 of the Job implementation, the type of the job is set by the user in the xml definition file and,
 * it can be whatever the user wants.
 */
public class SimpleJob extends AbstractJob {

    private final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
    private JobExecutionProgress jobProgress;

    public SimpleJob(ParameterSet parameterSet, List<ModuleController> modules) {

       super(parameterSet,modules);

        for (ModuleController mm : modules) {
            mm.setParent(this);
            mm.addObserver(this);
        }
    }

    public SimpleJob(ParameterSet parameterSet) {
        super(parameterSet);
    }

    @Override
    public void setModules(List<ModuleController> modules) {
        super.setModules(modules);

        for (ModuleController mm : modules) {
            mm.setParent(this);
            mm.addObserver(this);
        }
    }
    //<editor-fold desc="Observable implementation">
    @Override
    public synchronized void update(Observable o, Object arg) {
        ModuleController moduleController = (ModuleController) o;

        switch ((int) arg) {
            case  ModuleController.FINISHED:
                logger.debug("SimpleJob ID:{} Name:{} : Module {} done",getName(),getID(), moduleController.getName());

                //run next module
                String nextModule = getNextModuleName();
                if ( !nextModule.isEmpty() && canExecute() ) {
                    getModuleManager(nextModule).execute(this.jobProgress);
                }
                break;
            case ModuleController.SCHEDULED:
                if (canExecute()) {
                    moduleController.execute(this.jobProgress);
                }
                break;
            case ModuleController.FAILED:
                setEditable(true);
                updateStatus(JobState.ERROR);
                break;
            case ModuleController.STOPPED:
                setChanged();
                notifyObservers();

                boolean allModuleStopped = true;
                for(ModuleController mc: getModules()) {
                    if (mc.getState() != ModuleController.STOPPED || mc.getState() != ModuleController.FINISHED
                        || mc.getState() != ModuleController.FAILED) {
                        allModuleStopped = false;
                        break;
                    }
                }

                if (allModuleStopped) {
                    updateStatus(JobState.STOP);

                }

                break;
        }

        if(isJobFinished()) {
            setEditable(true);
            updateStatus(JobState.DONE);
        }

    }
    //</editor-fold>


    //<editor-fold desc="Executable interface implementation">
    @Override
    public void execute(JobExecutionProgress progress) throws JobException {

        if (getStatus() != JobState.IDLE && getStatus() != JobState.STOP) {
            return;
        }

        //save the progress for later use
        this.jobProgress = progress;

        String nextModule = getNextModuleName();
        if ( !nextModule.isEmpty() ) {
            setEditable(false);
            updateStatus(JobState.SUBMITTING);
            progress.info("Executing job");
            getModuleManager(nextModule).execute(this.jobProgress);
        }
        else {
            throw new JobException(JobException.EXECUTION_EXCEPTION,"No module to run");
        }
    }
    //</editor-fold>


    @Override
    public void updateParameter(Parameter<?> newParameter) throws JobException {
        try {
            Parameter oldParameter = getParameter(newParameter.getName());

            if (oldParameter.getClass().toString().equals(newParameter.getClass().toString())) {
                oldParameter.setValue(newParameter.getValue());
            }
            else {
                throw new JobException(JobException.UPDATE_EXCEPTION,"The new parameters is of different class than the old one.");
            }
        }
        catch (IllegalArgumentException ex) {
            throw new JobException(JobException.UPDATE_EXCEPTION,ex.getMessage());
        }
    }

    @Override
    public void delete() {
        updateStatus(JobState.DELETION);
        //Type to stop all the modules
        stopModules();
        updateStatus(JobState.DELETED);
        logger.info("Job deleted {}",getID());
    }

    /**
     * Stop the job
     * @throws JobException
     */
    public void stop() {

        stopModules();
        updateStatus(JobState.STOPPING);

    }
    /****
     *
     *  PRIVATE
     *
     ****/

     /**
     * Update the status of the job.
     * <p>
     *     Notify the observer (e.g. {@link core.CoreEngine} and all the module {@link SimpleJob#notifyModules(int)}
     * </p>
     * @param newState new state
     */
    private void updateStatus(int newState) {

        if (getStatus() != newState) {

            logger.info("Job ID:{} Name:{} ---  Changed status from {} to {}",getName(),getID(),JobState.toString(getStatus()),JobState.toString(newState));

            setStatus(newState);
            /**
             * if the staus is DONE (i.e finished running in batch)
             * than advance to PROCESSING
             * After DONE check if there any processing module installed
             */
            if (getStatus() == JobState.DONE) {
                if(isJobFinished()) {
                    logger.info("Job ID:{} Name:{} -- No more module. FINISHED",getID(),getName());
                    setStatus(JobState.FINISHED);
                }
                else {
                    logger.info("Job ID:{} Name:{} advanced to PROCESSING",getID(),getName());
                    setStatus(JobState.PROCESSING);
                }
            }

            //notify observers (coreEngine)
            setChanged();
            notifyObservers(getStatus());

            //notify modules
            notifyModules(getStatus());

        }
    }


    /**
     * Update job parameters from the results of a method
     * <p>
     *     A method can return a new parameter or a existing parameter but with the value changed.
     *     This method add/update the parameter if the method result has one.
     * </p>
     * <p>
     *     A special situation is treated when the new parameter is the {@code batchID}. The presence of
     *     this parameter means that the job has been submitted, hence the status is changed from
     *     {@code JobState.Submitting} to {@code JobState.SUBMITTED}
     * </p>
     * @param methodResult
     */
    protected void updateParametersFromResult(MethodResult methodResult) {

        if (methodResult.getResultParameters().getParameters().size() == 0) {
            return;
        }

        for (Parameter<?> parameter: methodResult.getResultParameters().getParameters()) {
            try {
                Parameter<?> oldParameter = getParameterSet().getParameter(parameter.getName());
                oldParameter.setValue(parameter.getValue());
            }
            catch (IllegalArgumentException ex) {
                getParameterSet().addParameter(parameter);

                //check if the name is batchID and change the status
                if (parameter.getName().endsWith("batchID")) {
                    updateStatus(JobState.SUBMITTED);
                    logger.info("Job ID:{} Name:{} has been submitted", getID(), this.getName());
                }
            }
        }
    }

    /**
     * Verify if all the modules has finished
     * @return true if all modules finished
     */
    protected boolean isJobFinished() {

        /**
         * If the job is paused or stop on error return false
         */
        if (getStatus() == JobState.ERROR || getStatus() == JobState.STOP) {
            return false;
        }

        /**
         * If the job has been submitted, wait for its completion in the batch system
         */
        if (getStatus() > JobState.SUBMITTING && getStatus() < JobState.DONE) {
            return false;
        }

        for(ModuleController mm: getModules()) {
            if (mm.getState() != ModuleController.FINISHED) {
                return false;
            }
        }

        return true;
    }

    //<editor-fold desc="QSTAT section">
    /**
     * Parse the qstat output to get the status of the job in the batch system
     * <br>If the batchID is not found in the output and it is set in the job, it means that the
     * job has been finished running in the batch system.
     * @param outString
     * @return the qstat status. If not found return empty string
     */
    private String parseQStatOutput(String outString) {

        final String lineSep = System.getProperty("line.separator");

        if (outString.isEmpty()) {
            return "";
        }

        try {
            StringParameter bathID = getParameterSet().getParameter("batchID");
            String[] lines = outString.split(lineSep);
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains(bathID.getValue())) {
                    logger.debug("BatchID {} found in qstat for job {}",bathID.getValue(),getID());
                    String[] fields =  lines[i].trim().split("\\s+");
                    return fields[4];
                }
            }
            return "";
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

    /**
     * Return the job status from the qstat string status
     * @param statusString
     * @return JobState status
     */
    private int getBatchStatus(String statusString) {
        switch (statusString) {
            case "r":
                return JobState.RUN;
             case "qw":
                return JobState.WAITING;
            case "d":
                return JobState.DELETION;
            case "h":
                return JobState.HOLD;
            case "E":
                return JobState.ERROR;
        }

        return JobState.NONE;
    }

    /**
     * Parse the qstat ouput and check if the batchID is present in the output
     *
     * @param qstatOutput
     */
    public void setQstatResult(MethodResult qstatOutput) {

        if ((getStatus() >= JobState.SUBMITTED && getStatus() < JobState.PROCESSING) || getStatus() == JobState.STOPPING) {
            logger.debug("SimpleJob ID:{} Name:{} :Method name: {}", getName(), getID(), qstatOutput.getMethodName());

            StringParameter qstatOutputS = qstatOutput.getResultParameters().getParameter("qstatOutput");
            String statusString = parseQStatOutput(qstatOutputS.getValue());

            if (!statusString.isEmpty()) {
                updateStatus(getBatchStatus(statusString));
            } else {
                logger.debug("Job ID:{} Name:{} BatchID not found in qstat output");
                if (getQstatMissFire() == 0) {
                    updateStatus(JobState.DONE);
                } else {
                    logger.debug("Job ID:{} Name:{} -- QStat fire missed.Set value to: {}", getID(), getName(), getQstatMissFire() - 1);
                    setQstatMissFire(getQstatMissFire() - 1);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="MODULE management section">

    /**
     * Get the module manager
     * @param name
     * @return moduleManager if found, null otherwise
     */
    private ModuleController getModuleManager(String name) {

        for(ModuleController moduleController : getModules()) {
            if (moduleController.getName().equals(name)) {
                return moduleController;
            }
        }

        return null;
    }

    /**
     * Get the next module to be executed
     * @return module name or empty string if there aren't any to be executed
     */
    private String getNextModuleName() {

        for(ModuleController mm: getModules()) {
            if (mm.getState() == ModuleController.SCHEDULED) {
                mm.start();
                return mm.getName();
            }
        }

        return "";
    }

    /**
     * Return the module current result state
     * @return
     */

    /**
     * Check if a module can be executedqdel
     * @return
     */
    private boolean canExecute()  {
        if (getStatus() == JobState.ERROR &&
        getStatus() == JobState.STOP) {
            return false;
        }

        return true;
    }

    /**
     * Notify modules abount the change in status
     * @param status
     */
    private void notifyModules(int status) {
        //notify modules about the state change
        for (ModuleController mm : getModules()) {
            mm.onJobStatusChange(status);
        }
    }

    private void stopModules() {
        //notify modules about the state change
        for (ModuleController mm : getModules()) {
            mm.stop();
        }
    }

    //</editor-fold>

}
