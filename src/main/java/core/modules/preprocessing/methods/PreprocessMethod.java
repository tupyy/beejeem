package core.modules.preprocessing.methods;

import core.modules.Method;
import core.modules.MethodResult;
import core.modules.StandardMethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.UUID;

/**
 * This methods creates the temporary folder, copy the input file to the temp folder.
 * Also, it creates the job file.
 */
public class PreprocessMethod implements Method {
    private Logger logger = LoggerFactory.getLogger(PreprocessMethod.class);

    private static final String METHOD_NAME = "PreprocessMethod";
    private final UUID jobID;
    private final String moduleName;
    private ParameterSet parameters;


    /**
     *
     * @param moduleName
     * @param parameterSet
     */
    public PreprocessMethod(String moduleName, UUID jobID,ParameterSet parameterSet) {
        this.moduleName = moduleName;
        this.jobID = jobID;
        this.parameters = parameterSet;
    }

    @Override
    public String getName() {
        return METHOD_NAME;
    }

    @Override
    public MethodResult execute() {
        try {
            String inputFile = (String) parameters.getParameter("filename").getValue();
            String tempFolder = (String)parameters.getParameter("temporaryFolder").getValue();

            //create the temporary folder
            if (!createFolder(tempFolder)) {
                return new StandardMethodResult(moduleName, METHOD_NAME,jobID,StandardMethodResult.ERROR, "Cannot create the temporary folder");
            }

            //copy file to temp folder
            try {
                copyFile(inputFile, tempFolder);
            } catch (IOException e) {
                return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, e.getMessage());
            }

            //create the job file
            try {
                createJobFile(parameters);
            }
            catch (IOException ex) {
                return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, ex.getMessage());
            }
            catch (NullPointerException ex) {
                return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, ex.getMessage());
            }

            logger.info("Creating code file for: {}",jobID);
            CreateCodeMethod createCodeMethod = new CreateCodeMethod(moduleName,jobID,parameters);
            MethodResult result = createCodeMethod.execute();
            if (result.getExitCode() != 0) {
                return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, result.getErrorMessages().get(0));
            }
        }
        catch (IllegalArgumentException ex) {
            logger.error("Method Preprocess failed: {}",ex.getMessage());
            return new StandardMethodResult(moduleName, METHOD_NAME,jobID, StandardMethodResult.ERROR, ex.getMessage());
        }

        return new StandardMethodResult(moduleName,METHOD_NAME,jobID, StandardMethodResult.OK);

    }

    @Override
    public void cancel() {

    }

    /**
     * Create a folder
     * @param folder
     * @return true if the folder has been created, false otherwise
     */
    private boolean createFolder(String folder) {

        File folderF = new File(folder);
        if ( !folderF.mkdir() ) {
            return false;
        }

       return true;
    }

    /**
     * Copy file
     * @param file
     * @param destinationFolder
     * @throws IOException
     */
    private void copyFile(String file, String destinationFolder) throws IOException{

        File sourceFile = new File(file);
        File destinationFile;


        if (destinationFolder.endsWith(File.pathSeparator)) {
            destinationFile = new File(destinationFolder.concat(sourceFile.getName()));
        }
        else {
            destinationFile = new File(destinationFolder.concat(File.separator).concat(sourceFile.getName()));
        }
        if (sourceFile.canRead() && sourceFile.exists()) {
            Files.copy(sourceFile.toPath(), destinationFile.toPath());
        }
        else {
            throw new IOException(String.format("File %s do not exists or is unreadable",file));
        }
    }

    /**
     * Create the job file
     * @param parameterSet
     */
    private void createJobFile(ParameterSet parameterSet) throws IOException,NullPointerException {
        String jobName = parameterSet.getParameter("name").getValue().toString();

        StringBuilder fileContent = new StringBuilder("JOBNAME: ".concat(jobName).concat("\n"));
        fileContent.append("JOBTYPE: ").append(getParameterValue(parameterSet,"queue").toLowerCase()).append("\n");
        fileContent.append("ANALYSIS: 4\n");
        fileContent.append("VERSION: ").append(getParameterValue(parameterSet,"isamiVersion")).append("\n");
        fileContent.append("\n");
        fileContent.append("DATA: \n");
        fileContent.append(jobName).append(".stf\n\n");
        fileContent.append("RESULT:\n");
        fileContent.append(jobName).append(".html\n");
        if (getParameterValue(parameterSet,"czmFile").equalsIgnoreCase("yes")) {
            fileContent.append(jobName).append(".czm\n");
        }

        if (getParameterValue(parameterSet,"traceFile").equalsIgnoreCase("yes")) {
            fileContent.append(jobName).append("_Hist1.trace\n");
            fileContent.append(jobName).append("_Hist2.trace\n");
        }

        String jobCodeFile = getParameterValue(parameterSet,"temporaryFolder")  + File.separator +  jobName + ".job";
        FileWriter fileOut = null;
        fileOut = new FileWriter(jobCodeFile);
        PrintWriter out = new PrintWriter(fileOut);
        out.print(fileContent);
        out.close();
        logger.info("Method ok.Job file created: {}",jobCodeFile);
    }

    /**
     * Get the value of a parameter
     * @param parameterSet
     * @param parameterName
     * @return String value or empty string
     */
    private String getParameterValue(ParameterSet parameterSet,String parameterName) {

        Parameter<?> parameter = parameterSet.getParameter(parameterName);
        if (parameter.getValue().toString().isEmpty()) {
            throw new NullPointerException("Parameter ".concat(parameterName).concat(" has no value set."));
        }
        return parameter.getValue().toString();
    }
}
