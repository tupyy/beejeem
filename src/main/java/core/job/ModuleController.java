package core.job;

import com.sshtools.ssh.SshException;
import core.modules.*;
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
    public static final int STATE_IDLE = 0;

    /**
     * The model can be executed
     */
    public static final int STATE_STARTABLE = 1;

    /**
     * Module is running
     */
    public  static final int STATE_INPROGRESS = 2;

    /**
     * Module finished
     */
    public static final int STATE_DONE = 3;


    //True if the module already run
    private int state;

    //the state of the job which will trigger the execution of the model
    private int triggerJobState;

    //holds the result state of the module
    private Boolean successful;

    private String name;

    private AbstractJob parent;

    /**
     * Keep tracks of the methods executed and their results
     */
    private HashMap<String,MethodResult> methods = new HashMap<>();

    /**
     *
     * @param name module name
     */
    public ModuleController(SimpleJob parent, String name, int triggerJobState) {
        this.name = name;
        this.parent = parent;

        if (triggerJobState == JobState.NONE) {
            changeState(ModuleController.STATE_STARTABLE);
        }
        else {
            this.triggerJobState = triggerJobState;
            changeState(ModuleController.STATE_IDLE);
        }
    }

    /**
     *
     * @param name module name
     */
    public ModuleController(String name, int triggerJobState) {
        this.name = name;
        this.parent = parent;

        if (triggerJobState == JobState.NONE) {
            changeState(ModuleController.STATE_STARTABLE);
        }
        else {
            this.triggerJobState = triggerJobState;
            changeState(ModuleController.STATE_IDLE);
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
             Class<? extends Module> classModule = (Class<? extends Module>)Class.forName(name);
             ModuleTask moduleTask = null;
             Module module = getModule(classModule);
            ThreadPoolExecutor executor = null;

             if (module instanceof LocalModule) {
                 LocalModule localModule = (LocalModule) module;
                 moduleTask = localModule.runModule(parent.getID(),parent.getParameterSet());
                 executor = ModuleExecutor.getLocalPoolExecutor();

             }
             else if (module instanceof SshModule) {
                 SshModule sshModule = (SshModule) module;
                 moduleTask = sshModule.runModule(parent.getID(), SshRemoteFactory.getSshClient(),parent.getParameterSet());
                 executor = ModuleExecutor.getSshPoolExecutor();
             }

            CompletableFuture<MethodResult> completableFuture = CompletableFuture.supplyAsync(moduleTask,executor)
                                                                .thenApply(methodResult -> {

                                                                    if (parent instanceof SimpleJob) {
                                                                        SimpleJob p = (SimpleJob) parent;
                                                                        p.setMethodResult(methodResult);
                                                                    }

                                                                    return null;
                                                                });

        } catch (ClassNotFoundException e) {
            errorMessage = "Class not found";
        } catch (ModuleException e) {
            errorMessage = "Module exception: ".concat(e.toString());
        } catch (SshException e) {
            errorMessage = "Ssh client exception: ".concat(e.toString());
        }
        finally {
            if (! errorMessage.isEmpty() ) {

                //TODO change it. it is horrible!!
                logger.error(errorMessage);
                StandardMethodResult methodResult = new StandardMethodResult(getName(),"unknown",parent.getID(),StandardMethodResult.ERROR,errorMessage);
                setMethodResult(methodResult);
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
    public void setMethodResult(MethodResult result) {

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

        successful = checkResults();

        if (isFinished()) {
            changeState(ModuleController.STATE_DONE);
        }

    }

    /**
     * Start the module
     */
    public void start() {
        changeState(ModuleController.STATE_INPROGRESS);
    }

    /**
     * Get the name
     * @return
     */
    public String getName() {
        return name;
    }
    /**
     * Return true if all the methods has been executed successfully
     * @return
     */
    public Boolean isSuccessful() {
        return successful;
    }

    /**
     * Function call by the job when a change of job state occurs.
     * <p>
     * If the new state is the same with triggerState, then the modules
     * becomes STARTABLE
     * @param newJobStatus
     */
    public void onJobStatusChange(int newJobStatus) {
        if (newJobStatus == triggerJobState && this.getState() == STATE_IDLE) {
            changeState(ModuleController.STATE_STARTABLE);
            logger.debug("Module {} has become startable",this.getName());
        }
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
     * Return true if all the methods run at this point are
     * successful
     * @return
     */
    private boolean checkResults() {

        for(Map.Entry entry: methods.entrySet()) {
            if (entry.getValue() != null) {
                MethodResult methodResult = (MethodResult) entry.getValue();
                if (methodResult.getExitCode() != 0) {
                    return false;
                }
            }
        }

        return true;
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
