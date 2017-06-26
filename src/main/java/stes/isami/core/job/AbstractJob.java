package stes.isami.core.job;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import stes.isami.core.modules.MethodResult;
import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Abstract class for {@link Job}.
 * <p>It provides basic behaviour for a job like being observable by the {@link stes.isami.core.CoreEngine}.</p>
 * <p>This class provides function for consuming the Qstat messages.</p>
 *
 */
public abstract class AbstractJob extends Observable {

    private final Logger logger = LoggerFactory.getLogger(AbstractJob.class);

    private UUID id = UUID.randomUUID();

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
        this.parameterSet.addParameter(createTemporaryFolderParameter(getId()));

        jobMachine = new StateMachine<Integer, Integer>(JobState.READY,new StateMachineConfig<>());
    }

    public AbstractJob(ParameterSet parameterSet, StateMachineConfig<Integer,Integer> jobStateMachineConfig) {
        jobMachine = new StateMachine<Integer, Integer>(JobState.READY,jobStateMachineConfig);

        parameterSet.addParameter(createTemporaryFolderParameter(getId()));
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

        this.parameterSet = parameterSet.clone();
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
        return id;
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

        if (getQstatMissFire() < 0) {
            return;
        }

        logger.debug("SimpleJob ID:{} Name:{} :Method name: {}", getName(), getId(), qstatOutput.getMethodName());

        StringParameter qstatOutputS = qstatOutput.getResultParameters().getParameter("qstatOutput");
        String statusString = parseQStatOutput(qstatOutputS.getValue());

        if (!statusString.isEmpty()) {
            fireTrigger(getBatchTrigger(statusString));

        } else {
            logger.debug("Job ID:{} Name:{} BatchID not found in qstat output");
            if (getQstatMissFire() == 0) {
                fireTrigger(Trigger.evDone);
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
     * Return the job status from the qstat string status
     * @param statusString
     * @return JobState status
     */
    private int getBatchTrigger(String statusString) {
        switch (statusString) {
            case "r":
                 return Trigger.evRunning;
            case "qw":
                return  Trigger.evWaiting;
            case "d":
                return Trigger.evDeletion;
            case "h":
                return Trigger.evHold;
            case "t":
                return Trigger.evTransferring;
            case "E":
                return Trigger.evError;
        }

        return 0;
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

    private Parameter createTemporaryFolderParameter(UUID jobID) {
        //create the temporary folder parameter
        StringParameter temporaryParameter = new StringParameter("temporaryFolder","Temporary folder","internal");
        temporaryParameter.setValue(System.getProperty("java.io.tmpdir").concat("Job_").concat(jobID.toString().substring(0,7)));
        return temporaryParameter;
    }



}
