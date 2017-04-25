package core.modules.processing;

import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.ChannelOpenException;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh2.Ssh2Client;
import core.modules.ModuleException;
import core.modules.SshModule;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implements the abstract processing module. This module
 */
public abstract class SimpleSshModule implements SshModule{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(SimpleSshModule.class);
    private  String moduleName = "SimpleSshModule";
    private List<String> methodNames = new ArrayList<>();

    public SimpleSshModule(String moduleName) {
        this.moduleName = moduleName;
    }
    @Override
    public String getName() {
        return moduleName;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient, ParameterSet parameterSet) throws ModuleException {
       return null;
    }

    @Override
    public List<String> getMethodsName() {
        return methodNames;
    }

    public void addMethod(String methodName) {
        methodNames.add(methodName);
    }

}
