package stes.isami.core.modules;

import com.maverick.ssh.SshClient;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.tasks.ModuleTask;

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
