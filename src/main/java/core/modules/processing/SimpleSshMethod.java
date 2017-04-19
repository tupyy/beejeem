package core.modules.processing;

import com.sshtools.sftp.*;
import com.sshtools.ssh.SshClient;
import core.modules.Method;
import core.modules.MethodResult;
import core.modules.SshSessionMethod;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic processing method. It provides basic functions for the processing step of a job.
 */
public abstract class SimpleSshMethod extends SshSessionMethod implements Method {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private String methodName = "SimpleSshMethod";
    private final ParameterSet parameters;
    private final String moduleName;
    private final UUID jobID;
    private final SftpClient sftpClient;
    private final SshClient sshClient;

    public SimpleSshMethod(String moduleName, String methodName, UUID jobID, ParameterSet parameterSet, SshClient sshClient, SftpClient sftpClient) {

        super(sshClient);

        this.sftpClient = sftpClient;
        this.sshClient = sshClient;
        this.jobID = jobID;
        this.moduleName = moduleName;
        this.parameters = parameterSet;
        this.methodName = methodName;

    }

    @Override
    public String getName() {
        return getMethodName();
    }

    @Override
    public MethodResult execute() {

       return null;

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
     * @param command batch sidepanel to be executed
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
    public String parseBatchID(String line) {
        //try to match the jobid from batch
        String patternJobSubmitted = ".*Your job ([\\d]+)";
        Pattern jobSubmittedP = Pattern.compile(patternJobSubmitted);
        Matcher m = jobSubmittedP.matcher(line);

        if (m.find()) {
            return m.group(1);
        }

        return "";
    }

    /**
     * Get the Isami batch submit command.
     * It constructs the submit command based on the name of the job.
     * {@code runIsami.sh -l jobFile.job jobName.py}
     * @param parameters
     * @return the submit command
     */
    public String getBatchCommand(ParameterSet parameters) {
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

    public String getMethodName() {
        return methodName;
    }

    public ParameterSet getParameters() {
        return parameters;
    }

    public String getModuleName() {
        return moduleName;
    }

    public UUID getJobID() {
        return jobID;
    }

    public SftpClient getSftpClient() {
        return sftpClient;
    }

    public SshClient getSshClient() {
        return sshClient;
    }
}
