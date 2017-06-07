package stes.isami.core.modules.qdel;

import com.sshtools.ssh.SshClient;
import stes.isami.core.modules.ModuleException;
import stes.isami.core.modules.SimpleCommandMethod;
import stes.isami.core.modules.SshModule;
import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * This module creates the qstat method which execute the batch system
 * qstat command.
 * <br>This is a internal module and it will be created externally of ModuleExecutor.
 */
public class QDelModule implements SshModule {

    private Logger logger = LoggerFactory.getLogger(QDelModule.class);
    private static final String MODULE_NAME = "QDelModule";

    public QDelModule() {

    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient, ParameterSet parameterSet) throws ModuleException {


        try {
            Parameter batchId = parameterSet.getParameter("batchID");

            String command = "qdel ".concat(batchId.getValue().toString());
            SimpleCommandMethod qStatMethod = new SimpleCommandMethod(sshClient,command);
            return new ModuleTask("qdelMethod",qStatMethod);
        }
        catch (IllegalArgumentException ex) {
            throw  new ModuleException("Cannot create qdel command: ".concat(ex.getMessage()));
        }

    }

}
