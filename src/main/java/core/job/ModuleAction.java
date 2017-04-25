package core.job;

import com.github.oxo42.stateless4j.delegates.Action;
import com.sshtools.ssh.SshException;
import core.modules.*;
import core.parameters.ParameterSet;
import core.ssh.SshRemoteFactory;
import core.tasks.ModuleExecutor;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of the Action interface of the state machine
 */
public class ModuleAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(AbstractJob.class);

    private final Function<MethodResult,Boolean> callbackFunction;
    private final Consumer<Boolean> consumer;
    private final Module moduleInstance;
    private final ParameterSet parameterSet;

    public ModuleAction(Module moduleInstace, ParameterSet parameterSet, Function<MethodResult, Boolean> callbackFunction, Consumer<Boolean> consumer) {
        this.moduleInstance = moduleInstace;
        this.callbackFunction = callbackFunction;
        this.parameterSet = parameterSet;
        this.consumer = consumer;
    }

    public String getName() {
        return moduleInstance.getName();
    }

    public ParameterSet getParameterSet() {
        return parameterSet;
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

            CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(moduleTask,executor)
                    .thenApply(methodResult -> callbackFunction.apply(methodResult));
            completableFuture.thenAccept(t -> consumer.accept(t));

            completableFuture.exceptionally( (th) -> {
                //error
                logger.error(th.getMessage());
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


}
