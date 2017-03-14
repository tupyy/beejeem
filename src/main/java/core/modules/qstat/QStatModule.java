package core.modules.qstat;

import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.ChannelOpenException;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh.SshSession;
import com.sshtools.ssh2.Ssh2Client;
import core.modules.Module;
import core.modules.ModuleException;
import core.modules.SshModule;
import core.modules.preprocessing.PreprocessingModule;
import core.modules.qstat.method.QStatMethod;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * This module creates the qstat method which execute the batch system
 * qstat command.
 * <br>This is a internal module and it will be created externally of ModuleExecutor.
 */
public class QStatModule implements SshModule{

    private final String command;
    private Logger logger = LoggerFactory.getLogger(PreprocessingModule.class);
    private static final String MODULE_NAME = "QStatModule";

    public QStatModule(String command) {
        this.command = command;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient,ParameterSet parameterSet) throws ModuleException {
        QStatMethod qStatMethod = new QStatMethod(sshClient,command);
        return new ModuleTask("qstatMethod",qStatMethod);
    }

    @Override
    public List<String> getMethodsName() {
        return null;
    }

}
