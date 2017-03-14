package core.modules.processing;

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
import core.modules.processing.methods.ProcessingMethod;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import core.tasks.ModuleTask;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implements the processing module
 */
public class ProcessingModule implements SshModule{

    private org.slf4j.Logger logger = LoggerFactory.getLogger(ProcessingModule.class);
    private static final String MODULE_NAME = "ProcessingModule";
    private final List<String> methodsName = new ArrayList<String>(
            Arrays.asList("ProcessingMethod"));

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public ModuleTask runModule(UUID jobID, SshClient sshClient, ParameterSet parameterSet) throws ModuleException {
        //this parameter is internally created and we're sure that exits
        try {
            StringParameter tempFolder = parameterSet.getParameter("temporaryFolder");
            StringParameter destinationFolder = parameterSet.getParameter("destinationFolder");

            if (destinationFolder.getValue() == null ) {
                throw new ModuleException("Empty value for the input file");

            }

            try {
                ProcessingMethod method = new ProcessingMethod(MODULE_NAME, jobID, parameterSet, sshClient, getFtpClient(sshClient));
                logger.info("Module {} Method {} created for job {}", MODULE_NAME, "ProcessingFileMethod", jobID);

                return new ModuleTask(jobID.toString(), method);
            } catch (SshException e) {
                throw new ModuleException(e.getMessage());
            }

        }
        catch (IllegalArgumentException ex) {
            throw new ModuleException(ex.getMessage());
        }
    }

    @Override
    public List<String> getMethodsName() {
        return methodsName;
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
