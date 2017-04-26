package core.job;

import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action;
import core.modules.MethodResult;
import core.modules.Module;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Default job class
 */
public class DefaultJob extends AbstractJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(DefaultJob.class);
    private boolean submitted = false;


    public DefaultJob(ParameterSet parameterSet, Map<Integer,Module> modules) {
        super(parameterSet);

        StateMachineConfig<Integer,Integer> defaultConfiguration = new StateMachineConfig<>();

        /**
         * Configure READY state
         */
        defaultConfiguration.configure(JobState.READY)
                .onEntry(new NotifyAction())
                .permit(Trigger.doPreprocessing,JobState.PREPROCESSING)
                .permit(Trigger.doStop,JobState.STOP)
                .permit(Trigger.doError,JobState.ERROR);

        /**
         * Configure PREPROCESSING state
         */
        if (modules.containsKey(JobState.PREPROCESSING)) {
            defaultConfiguration.configure(JobState.PREPROCESSING)
                    .onEntry(new NotifyAction())
                    .onEntry(new ModuleAction(this,modules.get(JobState.PREPROCESSING),new FutureCallback(),new StageCompletion(Trigger.doSubmit,Trigger.doError)))
                    .permit(Trigger.doSubmit,JobState.SUBMITTING)
                    .permit(Trigger.doStop,JobState.STOP)
                    .permit(Trigger.doError,JobState.ERROR);
        }

        /**
         * Configure SUBMITTING state
         */
        if (modules.containsKey(JobState.SUBMITTING)) {
            defaultConfiguration.configure(JobState.SUBMITTING)
                    .onEntry(new NotifyAction())
                    .onEntry(new ModuleAction(this,modules.get(JobState.SUBMITTING),new FutureCallback(),new StageCompletion(Trigger.doProcessing,Trigger.doError)))
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
                .permit(Trigger.evWainting,JobState.WAITING)
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
                .onExit(() -> setSubmitted(false))
                .permit(Trigger.evWainting,JobState.WAITING)
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
                .onExit(() -> setSubmitted(false))
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
                .onExit(() -> setSubmitted(false))
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evWainting,JobState.WAITING)
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
                .onExit(() -> setSubmitted(false))
                .permit(Trigger.evRestarted,JobState.RESTARTED)
                .permit(Trigger.evWainting,JobState.WAITING)
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
            defaultConfiguration.configure(JobState.POSTPROCESSING)
                    .onEntry(new NotifyAction())
                    .onEntry(new ModuleAction(this,modules.get(JobState.POSTPROCESSING),new FutureCallback(),new StageCompletion(Trigger.doFinish,Trigger.doError)))
                    .permit(Trigger.doFinish,JobState.FINISHED)
                    .permit(Trigger.doStop,JobState.STOP)
                    .permit(Trigger.doError,JobState.ERROR);
        }

        /**
         * Configure STOP state
         */
        defaultConfiguration.configure(JobState.STOP)
                .onEntry(new NotifyAction())
                .permit(Trigger.doRestart,JobState.SUBMITTING)
                .permit(Trigger.doFinish,JobState.FINISHED);

        /**
         * Configure ERROR state
         */
        defaultConfiguration.configure(JobState.ERROR)
                .onEntry(new NotifyAction())
                .permit(Trigger.doRestart,JobState.SUBMITTING);

        /**
         * Configure FINISHED state
         */
        defaultConfiguration.configure(JobState.FINISHED)
                .onEntry(new NotifyAction())
                .permit(Trigger.doRestart,JobState.SUBMITTING);

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
    public void delete() throws JobException {

    }

    @Override
    public void stop() {
        fireTrigger(Trigger.doStop);
    }

    @Override
    public void restart() {
        fireTrigger(Trigger.doRestart);
    }

    @Override
    public void updateParameter(Parameter<?> newParameter) throws JobException {

    }

    @Override
    public void updateParameter(String parameterName, Object parameterValue) throws IllegalArgumentException {

    }

    @Override
    public void updateParametes(ParameterSet parameters) throws JobException {

    }

    @Override
    public int getState() {
        return super.getState();
    }

    @Override
    public void setQstatResult(MethodResult qstatOutput) {
        if (isSubmitted()) {
            consumeQStatOutput(qstatOutput);
        }
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

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
    //</editor-fold>

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
                    logger.debug("BatchID {} found in qstat for job {}",bathID.getValue(),getId());
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
     * This class has to be put in every {@code onEntry}.
     * At the entrance of a state, the observer will be notified
     */
    private class NotifyAction implements Action {

        @Override
        public void doIt() {
            setChanged();
            notifyObservers(getState());
        }
    }



    /**
     * Created by tctupangiu on 20/04/2017.
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
