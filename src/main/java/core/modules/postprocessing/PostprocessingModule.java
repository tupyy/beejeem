package core.modules.postprocessing;

import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.ChannelOpenException;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh2.Ssh2Client;
import core.modules.ModuleException;
import core.modules.SshModule;
import core.modules.processing.ProcessingModule;
import core.modules.postprocessing.methods.PostprocessingMethod;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This module create the post processing method.
 */
public class PostprocessingModule implements SshModule{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PostprocessingModule.class);
    private static final String MODULE_NAME = "PostprocessingModule";
    private final List<String> methodsName = new ArrayList<String>(
            Arrays.asList("PostprocessingMethod"));

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public List<String> getMethodsName() {
        return methodsName;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient, ParameterSet parameterSet) throws ModuleException {

        PostprocessingMethod postprocessingMethod = null;

        try {
            postprocessingMethod = new PostprocessingMethod(MODULE_NAME,jobID,parameterSet,getFtpClient(sshClient));
        } catch (SshException e) {
            throw new ModuleException(e.getMessage());
        }

        logger.info("Module {} Method {} created for job {}",MODULE_NAME,"PostprocessingMethod",jobID);
        return new ModuleTask(jobID.toString(), postprocessingMethod);
    }

    /**
     * Create a new SftpClient
     * @param sshClient
     * @return
     * @throws SshException
     */
    private synchronized SftpClient getFtpClient(SshClient sshClient) throws SshException {

        Ssh2Client ssh2 = (Ssh2Client) sshClient;
        try {
            SftpClient sftpClient = new SftpClient(ssh2);
            return sftpClient;
        } catch (SftpStatusException e) {
            logger.error("Cannot create ftp client: {}", e.getMessage());
            throw new SshException("Cannot create ftp client", SshException.CHANNEL_FAILURE);
        } catch (ChannelOpenException e) {
            logger.error("Cannot open channel: {}", e.getMessage());
            throw new SshException("Cannot create ftp client", SshException.CHANNEL_FAILURE);
        }
    }
}
