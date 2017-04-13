package core.garbage;

import core.tasks.ModuleExecutor;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by tctupangiu on 13/04/2017.
 */
public class QDelModuleExecutor implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(GarbageCollector.class.getName());
    private final BlockingDeque<GarbageCollector.JobEntry> queue;
    private final GarbageCollector parent;

    public QDelModuleExecutor(GarbageCollector parent, BlockingDeque<GarbageCollector.JobEntry> queue) {
       this.queue = queue;
        this.parent = parent;
   }

    @Override
    public void run() {

        while(!(Thread.currentThread().isInterrupted())) {
            try {
                GarbageCollector.JobEntry jobEntry = queue.take();
                ModuleTask moduleTask = jobEntry.getTask();

                if (moduleTask != null) {
                    logger.debug("Executing qdel module for {} ",jobEntry.getID());
                    CompletableFuture completableFuture = CompletableFuture.supplyAsync(moduleTask, ModuleExecutor.getSshPoolExecutor()).thenApply(methodResult -> {
                        if (methodResult.getExitCode() == 0) {
                            parent.jobDeletedSuccessfully(methodResult.getJobID());
                        }
                        else {
                            parent.deletionError(methodResult.getJobID(),methodResult.getErrorMessages().get(0));
                        }

                        return null;
                    });

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }



}
