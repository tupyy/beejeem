package core.job;

import core.modules.MethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class implements the basic job.
 * <br>The user has to keep in mind that the {@link ModuleController#setParent(AbstractJob)} have to be implemented in the
 * class which extends {@link AbstractJob} because the module will call {@code SetMethodResult(MethodResuult methodResult}
 * <br>The same thing is valid for the {@link ModuleController#addObserver(Observer)}
 */

public abstract class AbstractJob extends Observable implements Job,Observer{

    private boolean markedForDeletion;

    private int qstatMissFire = 1;

    private final Logger logger = LoggerFactory.getLogger(SimpleJob.class);

    private UUID id = UUID.randomUUID();

    //parameter set
    private ParameterSet parameterSet = new ParameterSet();

    //keeps the modules manager
    private List<ModuleController> modules;

    /**
     * Keeps track of the job execution progress
     */
    private JobExecutionProgress jobProgress = null;

    /**
     * Status of the job
     */
    private int status;

    /**
     * True if the job is editable, false otherwise
     */
    private boolean editable;

    /**
     *
     * @param parameterSet
     */
    public AbstractJob(ParameterSet parameterSet,List<ModuleController> modules) {
        this(parameterSet);
        this.setModules(modules);

        //check the parameter set
        if (modules.size() == 0) {
            setStatus(JobState.ERROR);
        }

    }

    public AbstractJob(ParameterSet parameterSet) {
        this();

        for (Parameter p: parameterSet) {
            this.parameterSet.addParameter(p);
        }

        //check the parameter set
        if (!parameterSet.isValid()) {
            setStatus(JobState.ERROR);
        }

    }
    public AbstractJob() {
        this.status = JobState.IDLE;
        this.editable = true;

        //create the temporary folder parameter
        StringParameter temporaryParameter = new StringParameter("temporaryFolder","Temporary folder","internal");
        temporaryParameter.setValue(System.getProperty("java.io.tmpdir").concat("Job_").concat(id.toString().substring(0,7)));
        this.parameterSet.addParameter(temporaryParameter);
    }


    //<editor-fold desc="Job interface">
    @Override
    public String getName() {
        return parameterSet.getParameter("name").getValue().toString();
    }

    @Override
    public UUID getID() {
        return this.id;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public Parameter<?> getParameter(String parameterName) {
        Parameter<?> p = parameterSet.getParameter(parameterName);
        if (p != null) {
            return p.clone();
        }

        return null;
    }

    @Override
    public void updateParameter(Parameter<?> newParameter) throws JobException {
        //TODO cand o implementez interfata.
    }

    @Override
    public void updateParametes(ParameterSet parameters) throws JobException {
        //TODO cand o implementez interfata.
    }

    @Override
    public ParameterSet getParameters() {
        return parameterSet.clone();
    }

    @Override
    public void updateParameter(String parameterName, Object parameterValue) throws IllegalArgumentException {

    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    @Override
    public void markForDeletion() {
        this.markedForDeletion = true;
    }

    @Override
    public JobRecord collectData() {
        return null;
    }

    //</editor-fold>

    /**
     * Get the parameter set
     * @return the parameter set
     */
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    /**
     * Set module list
     * @param modules ArrayList of modules
     */
    public void setModules(List<ModuleController> modules) {
        this.modules = modules;
    }

    /**
     * Get the list of modules
     * @return list of module
     */
    public List<ModuleController> getModules() {
        return modules;
    }


    //<editor-fold desc="Executable interface">
    @Override
    public void execute(JobExecutionProgress progress) throws JobException {

    }
    //</editor-fold>

    //<editor-fold desc="QStat section">
    /**
     * Counts how many qstat miss fired the job allows before
     * considering that the batch finished running the job.
     * <br> It is set to 2.
     */
    public int getQstatMissFire() {
        return qstatMissFire;
    }

    /**
     * Reset the qstatMissFire to 2
     */
    public void resetQstatMissFire() {
        qstatMissFire = 1;
    }

    /**
     * Setter for qstatmissfire
     * @param newValue
     */
    public void setQstatMissFire(int newValue) {
        qstatMissFire = newValue;
    }

    @Override
    public void setQstatResult(MethodResult qStatMethodResult) {

    }

    //</editor-fold>

    //<editor-fold desc="Observable interface">
    /**
     * All the modules manager are extending the {@link Observable} class.
     * The change in state is treated in the class which extends the {@code AbstractClass}
     * @param moduleManager module to be observed
     * @param newState of the module
     */
    @Override
    public void update(Observable moduleManager, Object newState) {

    }
    //</editor-fold>

    /**
     * Set status
     * @param newStatus of the job
     */
    protected void setStatus(int newStatus) {
        this.status = newStatus;
    }

    /**
     * Update job parameters from the results of a methods
     * @param methodResult
     */
    protected void updateParametersFromResult(MethodResult methodResult) {

    }

    /**
     * Verify if all the modules has finished
     * @return true if all modules finished
     */
    protected boolean isJobFinished() {
        return false;
    }
}
