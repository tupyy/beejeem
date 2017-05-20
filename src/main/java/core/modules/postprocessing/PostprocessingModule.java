package core.modules.postprocessing;

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
 * This class implements the basics of a postprocessing module
 */
public abstract class PostprocessingModule implements SshModule{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PostprocessingModule.class);
    private String moduleName = "PostprocessingModule";
    private List<String> methodNames = new ArrayList<>();


    public PostprocessingModule(String moduleName) {
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

    public void addMethod(String methodName) {
        methodNames.add(methodName);
    }


}
