package core.modules.postprocessing.methods;

import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.sftp.TransferCancelledException;
import com.sshtools.ssh.SshException;
import core.modules.Method;
import core.modules.MethodResult;
import core.modules.StandardMethodResult;
import core.parameters.ParameterSet;
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
public class PostprocessingMethod implements Method {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final String METHOD_NAME = "PostprocessingMethod";
    private final ParameterSet parameters;
    private final String moduleName;
    private final UUID jobID;
    private final SftpClient sftpClient;

    public PostprocessingMethod(String moduleName, UUID jobID, ParameterSet parameterSet,
                                SftpClient sftpClient) {
        this.sftpClient = sftpClient;
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

        if (sftpClient.isClosed()) {
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"SFtp client is closed");
        }

        String batchFileName = getBatchName();
        if (batchFileName.isEmpty()) {
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"Cannot find batch file");
        }

        StringParameter temporaryFolder =  parameters.getParameter("temporaryFolder");
        StringParameter destinationFolder =  parameters.getParameter("destinationFolder");

        StandardMethodResult methodResult = new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.OK);
        try {
            String localBatchFile = downloadFile(temporaryFolder.getValue(),destinationFolder.getValue(),batchFileName);

            String content = readBatchFileContent(localBatchFile);
            if (isSuccessfully(content)) {

                logger.debug("Job: {} Batch file OK",jobID);
                for(String file: getFileList(content)) {
                    try {
                        downloadFile(temporaryFolder.getValue(), destinationFolder.getValue(), file);
                        methodResult.addParameter(createParameter(file));
                    }
                    catch (SftpStatusException ex) {
                        logger.error("Job ID:{} File not found",jobID,file);
                        return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"File not found ".concat(file));
                    }
                    finally {
                        sftpClient.exit();
                    }
                }
            }
            else {
                logger.error("The Job: {} encounter an error during execution.Check the batch file at: {}",jobID,localBatchFile);
                return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"Job finished with error.Check the batch file for more informations");
            }
        } catch (SftpStatusException e) {
            logger.debug("Batch file not found:{}",batchFileName);
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"Batch file not found ".concat(batchFileName));

        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"Batch file not found");

        } catch (TransferCancelledException e) {
            logger.debug(e.getMessage());
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,"Transfer of the batch file has been cancelled");

        } catch (SshException e) {
            logger.debug(e.getMessage());
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,e.getMessage());

        } catch (IOException e) {
            logger.debug(e.getMessage());
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.ERROR,e.getMessage());
        }

        return methodResult;
    }

    @Override
    public void cancel() {

    }

    /**
     * get the batch name
     */
    private String getBatchName() {
        try {

            StringParameter jobName = parameters.getParameter("name");
            StringParameter queueName = parameters.getParameter("queue");
            StringParameter batchID = parameters.getParameter("batchID");
            StringParameter destinationFolder = parameters.getParameter("destinationFolder");

            String batchfilename = jobName.getValue().concat("_").concat(queueName.getValue()).concat("_TF_tse.o").concat(batchID.getValue());

            return batchfilename;
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

    /**
     * Download the batch file to the localPath
     * @param localFolder local path
     * @param remotePath remote path
     * @param remoteFile source file
     * @return the local absolute path of the file
     * @throws TransferCancelledException
     * @throws FileNotFoundException
     * @throws SshException
     * @throws SftpStatusException
     */
    private String downloadFile(String localFolder,String remotePath,String remoteFile) throws TransferCancelledException, FileNotFoundException, SshException, SftpStatusException {

        String remoteFilePath = remotePath.concat("/").concat(remoteFile);
        logger.debug("Job {}: Downloading file: {}",jobID,remoteFilePath);

        sftpClient.lcd(localFolder);
        sftpClient.get(remoteFilePath);

        logger.debug("Job {}: File {} downloaded to {}",jobID,remoteFilePath,localFolder);

        return localFolder.concat("\\").concat(remoteFilePath.substring(remoteFilePath.lastIndexOf("/")+1,remoteFilePath.length()));
    }

    /**
     * Read the content of the batch file
     * @param localpath
     * @return
     * @throws IOException
     */
    private String readBatchFileContent(String localpath) throws IOException {
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
    private ArrayList<String> getFileList(String batchContent)  {
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
    private boolean isSuccessfully(String batchFileContent) {
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
    private StringParameter createParameter(String filename) {

        File f = new File(filename);
        StringParameter downloadedFile = new StringParameter(f.getName(),"Downloaded file","Result file",f.getAbsolutePath());

        return  downloadedFile;
    }

}
