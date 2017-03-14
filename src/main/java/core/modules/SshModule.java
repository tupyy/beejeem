package core.modules;

import com.sshtools.sftp.SftpClient;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;

import java.util.Collection;
import java.util.UUID;

/**
 * This interface extends Module interface to add ssh support.
 */
public interface SshModule extends Module {

    /**
     * Run this module with given parameters. The module may create new Tasks
     * and add them to the 'tasks' collection. The module is not supposed to
     * submit the tasks to the TaskController by itself.
     *
     * @param jobID
     * @param sshClient    an open ssh session
     * @param parameterSet
     * @return task to be executed
     * @throws ModuleException
     */
    public ModuleTask runModule(UUID jobID, SshClient sshClient, ParameterSet parameterSet) throws ModuleException;
}
