package core.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class to execute modules. The modules can be job or core modules.
 * <br>Following the type of the module MODULE_TYPE_SSH or MODULE_TYPE_NORMAL a different pool is used.
 * For the SSH type modules a pool of 2 concurrent task is used to execute modules whereas for the
 * NORMAL_TYPE a pool of 10 concurrent tasks is used.
 */
public class ModuleExecutor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    /**
     * We are experiencing errors when multiple call are made on the ssh client.Just execute two tasks at the time.
     */
    private static ThreadPoolExecutor sshPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    public ModuleExecutor() {

    }

    /**
     * Getter for local pool executor. The number of threads is set to 10.
     * @return local ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getLocalPoolExecutor() {
        return pool;
    }

    /**
     * Getter for the ssh pool executor. The number of threads is set to 2.
     * @return
     */
    public static ThreadPoolExecutor getSshPoolExecutor() {
        return sshPool;
    }

    /**
     * Shut down executors
     */
    public void shutDownExecutor() {

        logger.info("Shutdown the executors..");
        logger.info("Remaining tasks in local executor: {}",pool.getQueue().size());
        pool.shutdown();
        logger.info("Remaining tasks in ssh executor: {}",sshPool.getQueue().size());
        sshPool.shutdown();

    }

}
