package core.job;

import com.sshtools.ssh.SshException;
import core.modules.*;
import core.plugin.Plugin;
import core.ssh.SshRemoteFactory;
import core.tasks.ModuleExecutor;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class keeps all the information about a module: the methods to be executed,
 * the methods executed and their result.
 */
public class ModuleController extends Observable implements Executable {

    private final Logger logger = LoggerFactory.getLogger(ModuleController.class.getName());
    /*
    * Not started state
     */
    public static final int READY = 0;

    /**
     * The model can be executed
     */
    public static final int SCHEDULED = 1;

    /**
     * Module is running
     */
    public  static final int RUNNING = 2;

    /**
     * Module finished
     */
    public static final int FINISHED = 3;

    /**
     * Module failed
     */
    public static final int FAILED = 4;
    private final Module moduleInstance;


    //True if the module already run
    private int state;

    //the state of the job which will trigger the execution of the model
    private int triggerJobState = 0;

    private String name;

    private AbstractJob parent;

    /**
     * Keep tracks of the methods executed and their results
     */
    private HashMap<String,MethodResult> methods = new HashMap<>();

    /**
     * @param parent
     * @param moduleInstance
     * @param triggerJobState
     */
    public ModuleController(AbstractJob parent, Module moduleInstance, int triggerJobState) {
        this.moduleInstance = moduleInstance;
        this.parent = parent;

        if (triggerJobState == JobState.NONE) {
            changeState(ModuleController.SCHEDULED);
        }
        else {
            this.triggerJobState = triggerJobState;
            changeState(ModuleController.READY);
        }
    }

    /**
     * @param moduleInstance
     * @param triggerJobState
     */
    public ModuleController(Module moduleInstance, int triggerJobState) {
        this.moduleInstance = moduleInstance;

        if (triggerJobState == JobState.NONE) {
            changeState(ModuleController.SCHEDULED);
        }
        else {
            this.triggerJobState = triggerJobState;
            changeState(ModuleController.READY);
        }
    }

    /**
     * Set parent for the module
     * TODO change parent for future Scenario
     * @param parent
     */
    public void setParent(AbstractJob parent) {
        this.parent = parent;
    }


    @Override
    public void execute(JobExecutionProgress progress) {
        String errorMessage = "";

        try {
             ModuleTask moduleTask = null;
             ThreadPoolExecutor executor = null;

             if (moduleInstance instanceof LocalModule) {
                 LocalModule localModule = (LocalModule) moduleInstance;
                 moduleTask = localModule.runModule(parent.getID(),parent.getParameterSet());
                 progress.info("Executing job in local executor");
                 executor = ModuleExecutor.getLocalPoolExecutor();
             }
             else if (moduleInstance instanceof SshModule) {
                 SshModule sshModule = (SshModule) moduleInstance;
                 moduleTask = sshModule.runModule(parent.getID(), SshRemoteFactory.getSshClient(),parent.getParameterSet());
                 progress.info("Executing job in ssh executor");
                 executor = ModuleExecutor.getSshPoolExecutor();
             }

            CompletableFuture<MethodResult> completableFuture = CompletableFuture.supplyAsync(moduleTask,executor)
                                                                .thenApply(methodResult -> {

                                                                    parent.updateParametersFromResult(methodResult);
                                                                    setMethodResult(methodResult,progress);

                                                                    return null;
                                                                });
            completableFuture.exceptionally( (th) -> {
                    logger.error(th.getMessage());
                    StandardMethodResult methodResult = new StandardMethodResult(getName(),"unknown",parent.getID(),StandardMethodResult.ERROR,th.getMessage());
                    setMethodResult(methodResult,progress);
                    return null;
                    });


        } catch (ModuleException e) {
            errorMessage = "Module exception: ".concat(e.toString());
            progress.error(String.format("Module %s : %s",this.getName(),errorMessage));
        } catch (SshException e) {
            errorMessage = "Ssh client exception: ".concat(e.toString());
            progress.error(String.format("Module %s : %s",this.getName(),errorMessage));
        }
        finally {
            if (! errorMessage.isEmpty() ) {

                //TODO change it. it is horrible!!
                logger.error(errorMessage);
                StandardMethodResult methodResult = new StandardMethodResult(getName(),"unknown",parent.getID(),StandardMethodResult.ERROR,errorMessage);
                setMethodResult(methodResult,progress);
            }
        }
    }

    /**
     * Add a methods to the list of executed methods
     * @param methodName
     */
    public void addMethod(String methodName) {
        methods.put(methodName,null);
    }

    /**
     * Associate a result to a methods
     * @param result
     */
    public void setMethodResult(MethodResult result,JobExecutionProgress progress) {

        if (! result.getMethodName().equals("unknown")) {
            for (Map.Entry entry : methods.entrySet()) {
                if (entry.getKey().equals(result.getMethodName())) {
                    entry.setValue(result);
                }
            }
        }
        else {
            for(Map.Entry entry: methods.entrySet()) {
                if(entry.getValue() == null) {
                    entry.setValue(result);
                }
            }
        }

       if ( isSuccessful(result)) {
            progress.info(String.format("Module %s, Method %s successful",this.getName(),result.getMethodName()));
            changeState(ModuleController.FINISHED);
        }
        else {
            progress.info(String.format("Module %s, Method %s failed",this.getName(),result.getMethodName()));
            changeState(ModuleController.FAILED);
        }
        progress.info(String.format("Module %s finished",this.getName(),result.getMethodName()));
    }

    /**
     * Start the module
     */
    public void start() {
        changeState(ModuleController.RUNNING);
    }

    /**
     * Get the name
     * @return
     */
    public String getName() {
        return moduleInstance.getName();
    }
    /**
     * Return true if all the methods has been executed successfully
     * @return
     */
    public Boolean isSuccessful(MethodResult result) {
        if (result.getExitCode() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Function call by the job when a change of job state occurs.
     * <p>
     * If the new state is the same with triggerState, then the modules
     * becomes STARTABLE
     * @param newJobStatus
     */
    public void onJobStatusChange(int newJobStatus) {
        if (newJobStatus == getTrigger() && this.getState() == READY) {
            changeState(ModuleController.SCHEDULED);
            logger.debug("Module {} has become startable",this.getName());
        }
    }

    public int getTrigger() {
        return triggerJobState;
    }
    /**
     * Get the state
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     *
     * PRIVATE
     *
     */
    private void changeState(int newState) {
        state = newState;
        setChanged();
        notifyObservers(getState());
    }

    /**
     * Check if the module has finished
     */
    private boolean isFinished() {

        for(Map.Entry entry: methods.entrySet()) {
            if (entry.getValue() == null) {
                return false;
            }
        }

        return true;
    }

    private Module getModule(Class<? extends Module> moduleClass) {
        return ModuleStarter.getModuleInstance(moduleClass);
    }



}
