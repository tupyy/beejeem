package stes.isami.core.job;

import com.github.oxo42.stateless4j.delegates.Action;
import com.sshtools.ssh.SshException;
import stes.isami.core.modules.*;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.ssh.SshRemoteFactory;
import stes.isami.core.tasks.ModuleExecutor;
import stes.isami.core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of the Action interface of the state machine.
 *
 * <p>Upon the entry in the associated state, the state machine will execute the {@code doIt()} method.
 * <br>The module {@code moduleInstace} will be executed using {@link CancelableFuture}. When it finished, it will
 * call the {@code callbackFunction} which will evaluate the result of the module. It returns {@code true} if the
 * module has been successfully executed.
 * <br> The {@code consumer} will fire the trigger associated with the result of the module.
 * </p>
 *
 */
public class ModuleAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(ModuleAction.class);

    private final Function<MethodResult,Boolean> callbackFunction;
    private final Consumer<Boolean> consumer;
    private final Module moduleInstance;
    private final Job parent;
    private CompletableFuture<MethodResult> methodFuture;

    /**
     * Default constructor
     * @param parent job associated with the module
     * @param moduleInstace module to be executed
     * @param callbackFunction callback function called when the module finished executing
     * @param consumer consumer
     */
    public ModuleAction(Job parent,Module moduleInstace, Function<MethodResult, Boolean> callbackFunction, Consumer<Boolean> consumer) {
        this.moduleInstance = moduleInstace;
        this.callbackFunction = callbackFunction;
        this.parent = parent;
        this.consumer = consumer;
    }

    public String getName() {
        return moduleInstance.getName();
    }

    public ParameterSet getParameterSet() {
        return parent.getParameters();
    }


    @Override
    public void doIt() {
        try {
            ModuleTask moduleTask = null;
            ThreadPoolExecutor executor = null;

            if (moduleInstance instanceof LocalModule) {
                LocalModule localModule = (LocalModule) moduleInstance;
                moduleTask = localModule.runModule(getParameterSet().getID(),getParameterSet());
                executor = ModuleExecutor.getLocalPoolExecutor();
            }
            else if (moduleInstance instanceof SshModule) {
                SshModule sshModule = (SshModule) moduleInstance;
                moduleTask = sshModule.runModule(getParameterSet().getID(), SshRemoteFactory.getSshClient(),getParameterSet());
                 executor = ModuleExecutor.getSshPoolExecutor();
            }

            methodFuture = new CancelableFuture(moduleTask,executor);
            CompletableFuture<Boolean> booleanCompletableFuture =  methodFuture.thenApply(methodResult -> callbackFunction.apply(methodResult));
            booleanCompletableFuture.thenAccept(t -> consumer.accept(t));

            booleanCompletableFuture.exceptionally( (th) -> {
                //error
                logger.error("Future exceptionally: {} ",th.getMessage());
                return false;
            }).thenAccept(t -> consumer.accept(t));


        } catch (ModuleException e) {
            logger.error(String.format("Module {} : {}",moduleInstance.getName(),e.getMessage()));
            consumer.accept(false);
        } catch (SshException e) {
           logger.error(String.format("Module {} : {}",moduleInstance.getName(),e.getMessage()));
            consumer.accept(false);
        }
        finally {

        }
    }

    public void cancel() {
        if (methodFuture != null) {
            if (!methodFuture.isDone()) {
                logger.info("Future running {}",moduleInstance.getName());
                methodFuture.cancel(true);
                logger.info("Future canceled: {}  Job: {}",methodFuture.isCancelled(),parent.getID());
            }
        }
    }


}
