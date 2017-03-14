package core.modules.processing.methods;

import com.sshtools.sftp.*;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh.SshIOException;
import com.sshtools.ssh.SshSession;
import core.modules.Method;
import core.modules.MethodResult;
import core.modules.SshSessionMethod;
import core.modules.StandardMethodResult;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import java.io.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic processing method.
 * <p>It uploads all the files from the temporaryFolder to the
 * destinationFolder. If the destinationFolder is not found, it tries to create one. If it fails, throw exception.</p>
 * <p>It submit the job to the batch system. It returns the batchID in the StandardMethodResult object</p>
 */
public class ProcessingMethod extends SshSessionMethod implements Method {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final String METHOD_NAME = "ProcessingMethod";
    private final ParameterSet parameters;
    private final String moduleName;
    private final UUID jobID;
    private final SftpClient sftpClient;
    private final SshClient sshClient;

    public ProcessingMethod(String moduleName, UUID jobID, ParameterSet parameterSet, SshClient sshClient, SftpClient sftpClient) {
        super(sshClient);
        this.sftpClient = sftpClient;
        this.sshClient = sshClient;
        this.jobID = jobID;
        this.moduleName = moduleName;
        this.parameters = parameterSet;
    }

    @Override
    public String getName() {
        return METHOD_NAME;
    }

    @Override
    public MethodResult execute() {

        if (!sshClient.isConnected() || !sshClient.isAuthenticated()) {
            return new StandardMethodResult(moduleName,METHOD_NAME, jobID,StandardMethodResult.ERROR, "Sshclient is either not connected or not" +
                    "authenticated.");
        }

        try {
            String destinationFolder = (String) parameters.getParameter("destinationFolder").getValue();
            String temporaryFolder = (String) parameters.getParameter("temporaryFolder").getValue();


            //cd to destination. if failed, try to create the folder
            try {
                logger.debug("Job: {} Cd into destinationFolder {}",jobID,destinationFolder);
                sftpClient.cd(destinationFolder);
            } catch (SftpStatusException e) {
                try {
                    logger.error("DestinationFolder doens't exits.Try to create it");
                    sftpClient.mkdir(destinationFolder);
                    sftpClient.cd(destinationFolder);
                    logger.debug("DestinationFolder created");
                } catch (SftpStatusException e1) {
                    logger.error("FTPException {}", e1.getMessage());
                    return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, e1.getMessage());
                } catch (SshException e1) {
                    logger.error("FTPException {}", e1.getMessage());
                    return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, e1.getMessage());
                }
            } catch (SshException e) {
                e.printStackTrace();
            }


            //copy file one by one to destination file
            File tempFolder = new File(temporaryFolder);
            for (File file : tempFolder.listFiles()) {
                try {
                    sftpClient.put(file.getAbsolutePath());
                } catch (FileNotFoundException e) {

                } catch (SftpStatusException | SshException | TransferCancelledException e) {
                    logger.error("FTPException {}", e.getMessage());
                    return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, e.getMessage());
                }
            }

            //close ftp client
            try {
                sftpClient.exit();
            } catch (SshException e) {
                ;
            }

            logger.debug("Executing command for ID: {}",jobID);
            String outString = submitJob(getBatchCommand(parameters));
            String batchID = parseBatchID(outString);

            if (batchID.isEmpty()) {
                logger.error(outString);
                return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, "Error submitting job " + jobID + " " + outString);
            }


            //create a string parameter with the name batchID
            StringParameter batchIDParameter = new StringParameter("batchID", "Batch ID", "internal", batchID);

            //create the result
            StandardMethodResult resultOK = new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.OK, "");
            resultOK.addParameter(batchIDParameter);

            return resultOK;
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage());
            return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, ex.getMessage());
        }

    }

    @Override
    public void cancel() {

    }

    /**
     *
     * PRIVATE
     *
     */

    /**
     * Submit the job to batch system.
     * <br>The command is: runIsami.sh -l jobFile pythonFile
     * @param command batch command to be executed
     * @return the error message or empty string if there is no error
     */
    public String submitJob(String command)  {
        try {
            logger.debug("Executing command: ".concat(command));
            String outString = executeSessionCommand(command);
            return outString;
        }
        catch (Throwable t1) {
            return "Error submitting: ".concat(t1.toString());
        }
    }


    /**
     * Look up for the batchID in the line
     * @param line
     * @return batchID or empty
     */
    private String parseBatchID(String line) {
        //try to match the jobid from batch
        String patternJobSubmitted = ".*Your job ([\\d]+)\\s*\\(\"(\\w+)\"\\)";
        Pattern jobSubmittedP = Pattern.compile(patternJobSubmitted);
        Matcher m = jobSubmittedP.matcher(line);

        if (m.find()) {
            return m.group(1);
        }

        return "";
    }

    /**
     * Get the sumitting batch command
     * @param parameters
     * @return
     */
    private String getBatchCommand(ParameterSet parameters) {
        try {
            StringBuilder commandBuilder = new StringBuilder();
            StringParameter jobName = parameters.getParameter("name");
            StringParameter destination = parameters.getParameter("destinationFolder");

            commandBuilder.append(". /opt/sge/default/common/settings.sh;");
            commandBuilder.append("cd ").append(destination.getValue()).append(";");
            commandBuilder.append("/opt/sge/assystem/bin/runIsami.sh -l ");

            //append job file
            commandBuilder.append(createFilePath(destination.getValue(),jobName.getValue(),"job"));
            commandBuilder.append(" ");
            commandBuilder.append(createFilePath(destination.getValue(),jobName.getValue(),"py" + "\n"));

            return commandBuilder.toString();
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

    /**
     * Create the absolute path
     * @param destinationFolder
     * @param fileName
     * @param ext
     * @return absolute path
     */
    private String createFilePath(String destinationFolder, String fileName, String ext) {
        String pathSeparator = "";
        if (!destinationFolder.endsWith("/")) {
            pathSeparator = "/";
        }

        return destinationFolder.concat(pathSeparator).concat(fileName).concat(".").concat(ext);
    }

}
