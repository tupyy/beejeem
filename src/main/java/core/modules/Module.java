package core.modules;

import com.sshtools.sftp.SftpClient;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshSession;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * This interface represents any module.
 * A module is a component which creates a list of tasks based on a defined method.
 */
public interface Module {

    /**
     * Get the name of the module
     * @return module name
     */
    public String getName();

}
