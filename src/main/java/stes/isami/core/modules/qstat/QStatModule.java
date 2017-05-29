package stes.isami.core.modules.qstat;

import com.sshtools.ssh.SshClient;
import stes.isami.core.modules.ModuleException;
import stes.isami.core.modules.SshModule;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * This module creates the qstat method which execute the batch system
 * qstat sidepanel.
 * <br>This is a internal module and it will be created externally of ModuleExecutor.
 */
public class QStatModule implements SshModule {

    private final String command;
    private Logger logger = LoggerFactory.getLogger(QStatModule.class);
    private static final String MODULE_NAME = "QStatModule";

    public QStatModule(String command) {
        this.command = command;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient, ParameterSet parameterSet) throws ModuleException {
        QStatMethod qStatMethod = new QStatMethod(sshClient,command);
        return new ModuleTask("qstatMethod",qStatMethod);
    }

}
