package core.job;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import core.modules.Method;
import core.modules.MethodResult;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cosmin on 21/04/2017.
 */
public abstract class AbstractJob extends Observable {

    private final Logger logger = LoggerFactory.getLogger(AbstractJob.class);

    private int qstatMissFire = 1;
    /**
     * Parameter set
     */
    private ParameterSet parameterSet;

    /**
     * State machine
     */
    private StateMachine<Integer, Integer> jobMachine;

    public AbstractJob(ParameterSet parameterSet) {
        this.parameterSet = parameterSet;
        jobMachine = new StateMachine<Integer, Integer>(JobState.READY,new StateMachineConfig<>());
    }

    public AbstractJob(ParameterSet parameterSet, StateMachineConfig<Integer,Integer> jobStateMachineConfig) {
        jobMachine = new StateMachine<Integer, Integer>(JobState.READY,jobStateMachineConfig);
        this.setParameterSet(parameterSet);
    }

    /**
     * Create a new state machine with an initial configuration
     * @param stateMachineConfiguration
     */
    public void setStateMachineConfiguration(StateMachineConfig<Integer,Integer> stateMachineConfiguration) {
        jobMachine = new StateMachine<Integer, Integer>(JobState.READY,stateMachineConfiguration);
    }

    /**
     * Get parameter set
     * @return
     */
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    /**
     * Set parameter set
     * @param parameterSet
     */
    public void setParameterSet(ParameterSet parameterSet) {
        this.parameterSet = parameterSet;
    }

    /**
     * Fire an event
     * @param trigger to be fired
     */
    public void fireTrigger(Integer trigger) {
        jobMachine.fire(trigger);
    }

    /**
     * Return the state
     * @return
     */
    public int getState() {
        return jobMachine.getState();
    }

    /**
     * Get id
     * @return
     */
    public UUID getId() {
        return parameterSet.getID();
    }

    public boolean isEditable() {

        return parameterSet.isEditable();
    }

    public String getName() {
        return parameterSet.getParameter("name").getValue().toString();
    }

    /**
     * Counts how many qstat miss fired the job allows before
     * considering that the batch finished running the job.
     * <br> It is set to 2.
     */
    public int getQstatMissFire() {
        return qstatMissFire;
    }

    /**
     * Setter for qstatmissfire
     * @param newValue
     */
    public void setQstatMissFire(int newValue) {
        qstatMissFire = newValue;
    }

    /**
     * Parse the qstat ouput and check if the batchID is present in the output
     * Trigger the batch event if found
     * @param qstatOutput
     */
    public void consumeQStatOutput(MethodResult qstatOutput) {

  //       logger.debug("SimpleJob ID:{} Name:{} :Method name: {}", getName(), getId(), qstatOutput.getMethodName());

        StringParameter qstatOutputS = qstatOutput.getResultParameters().getParameter("qstatOutput");
        String statusString = parseQStatOutput(qstatOutputS.getValue());

        if (!statusString.isEmpty()) {
            triggerBatchEvent(statusString);
        } else {
            logger.debug("Job ID:{} Name:{} BatchID not found in qstat output");
            if (getQstatMissFire() == 0) {
                fireTrigger(JobState.DONE);
            } else {
                logger.debug("Job ID:{} Name:{} -- QStat fire missed.Set value to: {}", getId(), getName(), getQstatMissFire() - 1);
                setQstatMissFire(getQstatMissFire() - 1);
            }
        }

    }

    /**
     * Parse the qstat output to get the status of the job in the batch system
     * <br>If the batchID is not found in the output and it is set in the job, it means that the
     * job has been finished running in the batch system.
     * @param outString
     * @return the qstat status. If not found return empty string
     */
    private String parseQStatOutput(String outString) {

        String basePattern = "\\s[0-9.]{7}.*(20|21|22|23|[01]\\d|\\d)((:[0-5]\\d){1,2})";

        final String lineSep = System.getProperty(" ");

        if (outString.isEmpty()) {
            return "";
        }

        try {
            StringParameter batchID = getParameterSet().getParameter("batchID");
            Pattern pattern = Pattern.compile(batchID.getValue()+basePattern);
            Matcher m = pattern.matcher(outString);
            if (m.find()) {
                String stringFound = outString.substring(m.start(),m.end());
                String[] fields =  stringFound.trim().split("\\s+");
                return fields[4];
            }
        }
        catch (IllegalArgumentException ex) {
            return "";
        }

        return "";
    }


    /**
     * Return the job status from the qstat string status
     * @param statusString
     * @return JobState status
     */
    private void triggerBatchEvent(String statusString) {
        switch (statusString) {
            case "r":
                fireTrigger(Trigger.evRunning);
                break;
            case "qw":
                fireTrigger(Trigger.evWainting);
                break;
            case "d":
                fireTrigger(Trigger.evDeletion);
                break;
            case "h":
                fireTrigger(Trigger.evHold);
                break;
            case "E":
                fireTrigger(Trigger.evError);
                break;
        }
    }

    /**
     * This class is used to trigger an event after the completion of the
     * CompletableFuture. The CompletableFuture will call {@accept}.
     * If the method has been executed successfully, the state machine will trigger
     * the {@code okTrigger}, otherwise the {@code errorTrigger}
     */
    public class StageCompletion implements Consumer<Boolean> {

        private final Integer okTrigger;
        private final Integer errorTrigger;

        public StageCompletion(Integer okTrigger, Integer errorTrigger) {
            this.okTrigger = okTrigger;
            this.errorTrigger = errorTrigger;
        }

        @Override
        public void accept(Boolean aBoolean) {
            if (aBoolean) {
                jobMachine.fire(okTrigger);
            }
            else {
                jobMachine.fire(errorTrigger);
            }
        }
    }



}
