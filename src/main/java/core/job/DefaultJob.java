package core.job;

import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action;
import com.sshtools.ssh.SshException;
import core.modules.MethodResult;
import core.modules.Module;
import core.modules.ModuleException;
import core.modules.clean.CleaningModule;
import core.modules.qdel.QDelModule;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.ssh.SshRemoteFactory;
import core.tasks.ModuleExecutor;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Default job class
 */
public class DefaultJob extends AbstractJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(DefaultJob.class);
    private boolean submitted = false;
    private List<ModuleAction> moduleActionList = new ArrayList<>();

    public DefaultJob(ParameterSet parameterSet, Map<Integer,Module> modules) {
        super(parameterSet);

        StateMachineConfig<Integer,Integer> defaultConfiguration = new StateMachineConfig<>();

        /**
         * Configure READY state
         */
        defaultConfiguration.configure(JobState.READY)
                .onEntry(new NotifyAction())
                .permit(Trigger.doPreprocessing,JobState.PREPROCESSING)
                .ignore(Trigger.doStop)
                .permit(Trigger.doError,JobState.ERROR);

        /**
         * Configure PREPROCESSING state
         */
         if (modules.containsKey(JobState.PREPROCESSING)) {
             ModuleAction preprocessingModuleAction = new ModuleAction(this,modules.get(JobState.PREPROCESSING),new FutureCallback(),new StageCompletion(Trigger.doSubmit,Trigger.doError));
             moduleActionList.add(preprocessingModuleAction);
             defaultConfiguration.configure(JobState.PREPROCESSING)
                    .onEntry(new NotifyAction())
                    .onEntry(preprocessingModuleAction)
                    .permit(Trigger.doSubmit,JobState.SUBMITTING)
                    .permit(Trigger.doStop,JobState.STOP)
                    .permit(Trigger.doError,JobState.ERROR);
        }

        /**
         * Configure SUBMITTING state
         */
        if (modules.containsKey(JobState.SUBMITTING)) {
            ModuleAction processingModuleAction = new ModuleAction(this,modules.get(JobState.SUBMITTING),new FutureCallback(),new StageCompletion(Trigger.doProcessing,Trigger.doError));
            moduleActionList.add(processingModuleAction);
            defaultConfiguration.configure(JobState.SUBMITTING)
                    .onEntry(new NotifyAction())
                    .onEntry(processingModuleAction)
                    .permit(Trigger.doProcessing,JobState.SUBMITTED)
                    .permit(Trigger.doStop,JobState.STOP)
                    .permit(Trigger.doError,JobState.ERROR);
        }

        /**
         * Configure SUBMITTED STATE
         */
        defaultConfiguration.configure(JobState.SUBMITTED)
                .onEntry(new NotifyAction())
                .onEntry(() -> setSubmitted(true))
                .ignore(Trigger.evUnknown)
                .permit(Trigger.evWaiting,JobState.WAITING)
                .permit(Trigger.evRunning,JobState.RUN)
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evSuspended,JobState.SUSPENDED)
                .permit(Trigger.evTransferring,JobState.TRANSFERRING)
                .permit(Trigger.evDone,JobState.POSTPROCESSING)
                .permit(Trigger.doStop,JobState.STOP)
                .permit(Trigger.doError,JobState.ERROR);

        //<editor-fold desc="Batch state configuration">
        /**
         * Configure RUN STATE
         */
        defaultConfiguration.configure(JobState.RUN)
                .onEntry(new NotifyAction())
                .ignore(Trigger.evRunning)
                .ignore(Trigger.evUnknown)
                .permit(Trigger.evWaiting,JobState.WAITING)
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evSuspended,JobState.SUSPENDED)
                .permit(Trigger.evTransferring,JobState.TRANSFERRING)
                .permit(Trigger.evDone,JobState.POSTPROCESSING)
                .permit(Trigger.doStop,JobState.STOP)
                .permit(Trigger.doError,JobState.ERROR);

        /**
         * Configure WAITING STATE
         */
        defaultConfiguration.configure(JobState.WAITING)
                .onEntry(new NotifyAction())
                .ignore(Trigger.evWaiting)
                .ignore(Trigger.evUnknown)
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evRunning,JobState.RUN)
                .permit(Trigger.evSuspended,JobState.SUSPENDED)
                .permit(Trigger.evTransferring,JobState.TRANSFERRING)
                .permit(Trigger.evDone,JobState.POSTPROCESSING)
                .permit(Trigger.doStop,JobState.STOP)
                .permit(Trigger.doError,JobState.ERROR);

        /**
         * Configure SUSPENDED STATE
         */
        defaultConfiguration.configure(JobState.SUSPENDED)
                .onEntry(new NotifyAction())
                .ignore(Trigger.evSuspended)
                .ignore(Trigger.evUnknown)
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evWaiting,JobState.WAITING)
                .permit(Trigger.evRunning,JobState.RUN)
                .permit(Trigger.evTransferring,JobState.TRANSFERRING)
                .permit(Trigger.evDone,JobState.POSTPROCESSING)
                .permit(Trigger.doStop,JobState.STOP)
                .permit(Trigger.doError,JobState.ERROR);

        /**
         * Configure TRANSFERRING STATE
         */
        defaultConfiguration.configure(JobState.TRANSFERRING)
                .onEntry(new NotifyAction())
                .ignore(Trigger.evTransferring)
                .ignore(Trigger.evUnknown)
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evWaiting,JobState.WAITING)
                .permit(Trigger.evRunning,JobState.RUN)
                .permit(Trigger.evSuspended,JobState.SUSPENDED)
                .permit(Trigger.evDone,JobState.POSTPROCESSING)
                .permit(Trigger.doStop,JobState.STOP)
                .permit(Trigger.doError,JobState.ERROR);
        //</editor-fold>


        /**
         * Configure Processing state
         */
         if (modules.containsKey(JobState.POSTPROCESSING)) {
             ModuleAction postProcessingModuleAction = new ModuleAction(this,modules.get(JobState.POSTPROCESSING),new FutureCallback(),new StageCompletion(Trigger.doFinish,Trigger.doError));
             moduleActionList.add(postProcessingModuleAction);
             defaultConfiguration.configure(JobState.POSTPROCESSING)
                    .onEntry(new NotifyAction())
                    .onEntry(() -> setSubmitted(false))
                    .onEntry(postProcessingModuleAction)
                    .permit(Trigger.doFinish,JobState.FINISHED)
                    .permit(Trigger.doStop,JobState.STOP)
                    .permit(Trigger.doError,JobState.ERROR);
        }

        /**
         * Configure STOP state
         */
        defaultConfiguration.configure(JobState.STOP)
                .onEntry(new NotifyAction())
                .onEntry(() -> {

                    moduleActionList.forEach(ModuleAction::cancel);

                    ModuleTask qDelTask = createQDelTask();
                    if (qDelTask != null) {
                        CompletableFuture completableFuture = CompletableFuture.supplyAsync(qDelTask, ModuleExecutor.getSshPoolExecutor()).thenApply(methodResult -> {
                            if (methodResult.getExitCode() == 0) {
                                logger.debug("Job {} deleted successfully from batch system", getID());
                            } else {
                                logger.debug("Error removing job {} from batch system: {}", getID(), methodResult.getErrorMessages().get(0));
                            }

                            return null;
                        });
                    }
                    setSubmitted(false);
                })
                .permit(Trigger.doRestart,JobState.RESTARTING)
                .permit(Trigger.doFinish,JobState.FINISHED);

        /**
         * Configure RESTARTING state
         */
        defaultConfiguration.configure(JobState.RESTARTING)
                .onEntry(new NotifyAction())
                .onEntry(new ModuleAction(this,new CleaningModule(),new FutureCallback(),new StageCompletion(Trigger.doPreprocessing,Trigger.doError)))
                .onEntry(() -> setSubmitted(false))
                .ignore(Trigger.doRestart)
                .permit(Trigger.doPreprocessing,JobState.PREPROCESSING);

        /**
         * Configure ERROR state
         */
        defaultConfiguration.configure(JobState.ERROR)
                .onEntry(new NotifyAction())
                .onEntry(() -> setSubmitted(false))
                .permit(Trigger.doRestart,JobState.RESTARTING);

        /**
         * Configure FINISHED state
         */
        defaultConfiguration.configure(JobState.FINISHED)
                .onEntry(new NotifyAction())
                .onEntry(() -> setSubmitted(false))
                .permit(Trigger.doRestart,JobState.PREPROCESSING);

        setStateMachineConfiguration(defaultConfiguration);

    }
    public DefaultJob(ParameterSet parameterSet, StateMachineConfig<Integer, Integer> jobStateMachineConfig) {
        super(parameterSet, jobStateMachineConfig);
    }

    //<editor-fold desc="Job Interface">
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public UUID getID() {
        return super.getId();
    }

    @Override
    public boolean isEditable() {
        return getParameterSet().isEditable();
    }

    @Override
    public ParameterSet getParameters() {
        return getParameterSet().clone();
    }

    @Override
    public void restart() {

        if (canRestart()) {
            fireTrigger(Trigger.doRestart);
        }
    }

    /**
     * Stop the job
     */
    @Override
    public void stop() {
        fireTrigger(Trigger.doStop);
    }

    @Override
    public boolean updateParameter(Parameter<?> newParameter) throws JobException {
        throw new NotImplementedException();
    }

    @Override
    public boolean updateParameter(String parameterName, Object parameterValue) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public boolean updateParametes(ParameterSet parameters) throws JobException {
        if (isEditable()) {
            setParameterSet(parameters);
            return true;
        }
        else {
            throw new JobException(JobException.UPDATE_EXCEPTION,"Job is not editable");
        }
    }

    @Override
    public int getState() {
        return super.getState();
    }

    @Override
    public void setQstatResult(MethodResult qstatOutput) {

        Thread t = new Thread() {

            public void run() {
                if (isSubmitted()) {
                    consumeQStatOutput(qstatOutput);
                }
            }
        };

        t.start();
    }

    @Override
    public void execute() throws JobException {
        fireTrigger(Trigger.doPreprocessing);
    }


    /**
     * True if the job has been submitted
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * Set the submitted flag
     * @param submitted
     */
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
    //</editor-fold>

    /**************************************************************************************************************
     *
     *
     *                                                  PRIVATE
     *
     *
     ***************************************************************************************************************/

    /**
     * Check if a job can be restarted
     * @return
     */
    private boolean canRestart() {
        if (getState() == JobState.STOP ||
                getState() == JobState.ERROR ||
                getState() == JobState.FINISHED) {
            return true;
        }

        return false;

    }

    /**
     * This class has to be put in every {@code onEntry}.
     * At the entrance of a state, the observer will be notified
     */
    private class NotifyAction implements Action {

        @Override
        public void doIt() {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Create the qdel Task
     * @return
     */
    private ModuleTask createQDelTask() {

        QDelModule module = new QDelModule();
        try {
            ModuleTask qdelTask = module.runModule(getId(), SshRemoteFactory.getSshClient(),getParameters());
            return qdelTask;
        } catch (ModuleException | SshException e) {
            return null;
        }

    }
    /**
     * This callback is called when a module has finished running.
     * <p>It checks the exit code of the method and fire the okTrigger or errorTrigger if the
     * method has failed.</p>
     */
    private class FutureCallback implements Function<MethodResult,Boolean> {
        private final Logger logger = LoggerFactory.getLogger(FutureCallback.class);

        @Override
        public Boolean apply(MethodResult methodResult) {

            if (methodResult.getExitCode() != 0) {
                logger.error("Error method {} : {}",methodResult.getMethodName(),methodResult.getErrorMessages().get(0));
                return false;
            }

            if (methodResult.getResultParameters().getParameters().size() == 0) {
                return true;
            }

            for (Parameter<?> parameter: methodResult.getResultParameters().getParameters()) {
                try {
                    logger.info("Method {} Parameter updated: {}",methodResult.getMethodName(),parameter.getName());
                    Parameter<?> oldParameter = getParameterSet().getParameter(parameter.getName());
                    oldParameter.setValue(parameter.getValue());
                }
                catch (IllegalArgumentException ex) {
                    logger.info("Method {} New parameter found: {}",methodResult.getMethodName(),parameter.getName());
                    getParameterSet().addParameter(parameter);
                    }
                }
            return true;
        }

    }
}
