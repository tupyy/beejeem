package core.modules.qdel;

import com.sshtools.ssh.SshClient;
import core.modules.ModuleException;
import core.modules.SimpleCommandMethod;
import core.modules.SshModule;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * This module creates the qstat method which execute the batch system
 * qstat sidepanel.
 * <br>This is a internal module and it will be created externally of ModuleExecutor.
 */
public class QDelModule implements SshModule{

    private Logger logger = LoggerFactory.getLogger(QDelModule.class);
    private static final String MODULE_NAME = "QDelModule";

    public QDelModule() {

    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient,ParameterSet parameterSet) throws ModuleException {


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
