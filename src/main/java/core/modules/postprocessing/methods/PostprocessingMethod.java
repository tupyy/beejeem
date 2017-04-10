package core.modules.postprocessing.methods;

import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.sftp.TransferCancelledException;
import com.sshtools.ssh.SshException;
import core.modules.Method;
import core.modules.MethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.ListParameter;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * This class implements the post-processing common to all type of jobs. The actions are:
 * <ul>
 *     <li>
 *         The batch file is downloaded and parsed to see if there are any runtime errors.
 *         If everything is ok, the name of the result file is retrieved from the file
 *     </li>
 *     <li>
 *         Each output file is downloaded to the temporaryFolder. The output file should be: the HTML file, trace files
 *         and the sigma file if it present.
 *     </li>
 * </ul>
 * It return a StandardMethodResult which has every downloaded file absolute path as StringParameter.
 */
public abstract class PostprocessingMethod implements Method {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private String methodName = "PostprocessingMethod";
    private final ParameterSet parameters;
    private final String moduleName;
    private final UUID jobID;
    private final SftpClient sftpClient;

    public PostprocessingMethod(String moduleName, String methodName,UUID jobID, ParameterSet parameterSet,
                                SftpClient sftpClient) {

        this.methodName = methodName;
        this.sftpClient = sftpClient;
        this.jobID = jobID;
        this.moduleName = moduleName;
        this.parameters = parameterSet;
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
     * Get the name of batch file
     */
    public String getBatchName() {
        try {

            Parameter jobName = getParameters().getParameter("name");
            Parameter queueName = getParameters().getParameter("queue");
            Parameter batchID = getParameters().getParameter("batchID");
            Parameter destinationFolder = getParameters().getParameter("destinationFolder");

            String batchfilename = jobName.getValue().toString().concat("_").concat(queueName.getValue().toString()).concat("_TF_tse.o").concat(batchID.getValue().toString());

            return batchfilename;
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

    /**
     * Download a file to the localPath
     * @param localFolder local path
     * @param remotePath remote path
     * @param remoteFile source file
     * @return the local absolute path of the file
     * @throws TransferCancelledException
     * @throws FileNotFoundException
     * @throws SshException
     * @throws SftpStatusException
     */
    public String downloadFile(String localFolder,String remotePath,String remoteFile) throws TransferCancelledException, FileNotFoundException, SshException, SftpStatusException {

        String remoteFilePath = remotePath.concat("/").concat(remoteFile);
        logger.debug("Job {}: Downloading file: {} to {}", getJobID(),remoteFilePath,localFolder);

        getSftpClient().lcd(localFolder);
        getSftpClient().get(remoteFilePath);

        logger.debug("Job {}: File {} downloaded to {}", getJobID(),remoteFilePath,localFolder);

        return localFolder.concat("\\").concat(remoteFilePath.substring(remoteFilePath.lastIndexOf("/")+1,remoteFilePath.length()));
    }

    /**
     * Read the content of the batch file
     * @param localpath
     * @return the content of the batch file
     * @throws IOException
     */
    public String readBatchFileContent(String localpath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(localpath));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }

    /**
     * Parse the batch file on the local path and return the list of outfiles created by the batch system
     * @param batchContent the content of the batch file
     * @return list of output files downloaded from scratch to the remote folder by the batch system
     */
    public ArrayList<String> getFileList(String batchContent)  {
        ArrayList<String> fileList = new ArrayList<>();

        String outputContent = batchContent;
        for (String s: outputContent.split(("\\n"))) {
            if (s.contains("Getting file")) {
                String[] l = s.split(":");
                File f = new File(l[1].trim());
                fileList.add(f.getName());
            }
        }

        //search if the file has been copied
        for (String s: outputContent.split(("\\n"))) {
            if (s.contains("cp: cannot stat")) {
                for (Iterator i = fileList.iterator(); i.hasNext();) {
                    String fileP = (String) i.next();
                    if (s.contains(fileP)) {
                        i.remove();
                    }
                }
            }
        }

        return fileList;
    }

    /**
     * look for the "PyBusi TRACEBACK" string
     * if present
     * @return
     */
    public boolean isBatchSuccessfully(String batchFileContent) {
         for (String s: batchFileContent.split(("\\n"))) {
            if (s.contains("PyBusi TRACEBACK")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Create a StringParameter for each downloaded file with the absolute path
     * @param filename
     * @return StringParameter which value is the absolute path of the downloaded file
     */
    public StringParameter createParameter(String filename) {

        File f = new File(filename);
        StringParameter downloadedFile = new StringParameter(f.getName(),"Downloaded file","Result file",f.getAbsolutePath());

        return  downloadedFile;
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
}
