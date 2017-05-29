package stes.isami.core;

import com.sshtools.ssh.SshException;
import stes.isami.core.job.Job;
import stes.isami.core.modules.MethodResult;
import stes.isami.core.modules.ModuleException;
import stes.isami.core.modules.qstat.QStatModule;
import stes.isami.core.parameters.parametertypes.StringParameter;
import stes.isami.core.ssh.SshRemoteFactory;
import stes.isami.core.tasks.ModuleExecutor;
import stes.isami.core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class to handle the qstat messages. It creates a qstat module every 10second.
 * <br>The creation of the module is conditioned by the receving the output from the last module created.
 * <br> If, after 10s, the output has not arrived from the executor delay the execution of the new module until
 * the output has arrived
 */
public class QStatManager {

    private final int fireInterval = 10;

    /**
     * Count how many empty output we received from qstat
     * <br>If missFired = 0, stop the timer
     */
    private int missFired = 3;
    private final CoreEngine core;
    private ModuleExecutor executor;
    private Timer timer;
    private UUID id = UUID.randomUUID();
    private final Logger logger = LoggerFactory.getLogger(QStatManager.class);

    /**
     * If the flag is set to true, a new module can be executed
     */
    private boolean qstatFlag = false;

    public QStatManager(CoreEngine core, ModuleExecutor executor) {
        this.executor = executor;
        this.core = core;
    }

    /**
     * Start the timer. The timer is set to fireInterval value
     */
    public void start() {

        if (timer == null) {

            missFired = 3;
            qstatFlag = true;

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    executeQStatModule();
                }
            }, fireInterval * 1000, fireInterval * 1000);

            logger.debug("Timer started. Interval: {}s", fireInterval);
        }
    }

    /**
     * Stop the timer
     */
    public void stop() {

        if (timer != null) {
            timer.cancel();
            timer = null;
            qstatFlag = false;
            logger.debug("Timer stopped");
        }
    }

    /**
     * Distribute the output from qstat to every job
     * @param qstatOutput
     */
    public void receiveOutput(MethodResult qstatOutput) {

        //rise the flag
        qstatFlag = true;
        for (UUID id: core.getJobIDList()) {
            Job job = core.getJob(id);
            job.setQstatResult(qstatOutput);
        }

        //check the content
        if (qstatOutput.getExitCode() == 0) {

            StringParameter qstatOutputP = qstatOutput.getResultParameters().getParameter("qstatOutput");
            if (qstatOutputP.getValue().isEmpty()) {
                missFired--;
            }
        }
        else {
            logger.error("QStat error. Stopping qstat modules");
            stop();
        }

        if (missFired == 0) {
            stop();
        }
    }

    /**
     * Execute the module
     */
    private void executeQStatModule() {
        if (qstatFlag) {
            try {
                ThreadPoolExecutor executor =  ModuleExecutor.getSshPoolExecutor();
                QStatModule qStatModule = createModule();
                ModuleTask task = qStatModule.runModule(UUID.randomUUID(), SshRemoteFactory.getSshClient(),null);

                try {
                    CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(task, executor)
                            .thenAccept(methodResult -> {
                                receiveOutput(methodResult);
                            });

                    completableFuture.exceptionally((th) -> null);
                }
                catch (RejectedExecutionException ex) {
                    logger.debug("Executor rejected execution of QStatmodule");
                    return;
                }

                qstatFlag = false;
            } catch (ModuleException e) {
                e.printStackTrace();
            } catch (SshException e) {
                logger.error("Error executing module: {}",e.getMessage());
                stop();
            }
        }
        else {
            logger.debug("QStat flag is down. Cannot execute module");
        }
    }

    private QStatModule createModule() {
        logger.debug("QStat module created");
        QStatModule qStatModule = new QStatModule("/opt/sge/bin/lx24-amd64/qstat");
        return qStatModule;
    }
}
