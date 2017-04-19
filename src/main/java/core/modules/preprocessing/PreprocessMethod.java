package core.modules.preprocessing;

import core.modules.Method;
import core.modules.MethodResult;
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
 * This class provides a set of methods to help the preprocessing of the job.
 */
public abstract class PreprocessMethod implements Method {
    private Logger logger = LoggerFactory.getLogger(PreprocessMethod.class);

    private String methodName = "PreprocessMethod";
    private final UUID jobID;
    private final String moduleName;
    private ParameterSet parameters;


    /**
     * @param moduleName the name of the module
     * @param parameterSet the set of parameters of the job
     */
    public PreprocessMethod(String moduleName, String methodName,UUID jobID,ParameterSet parameterSet) {

        this.methodName = methodName;
        this.moduleName = moduleName;
        this.jobID = jobID;
        this.parameters = parameterSet;
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public MethodResult execute() {
       return null;

    }

    @Override
    public void cancel() {

    }

    public UUID getJobID() {
        return jobID;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ParameterSet getParameters() {
        return parameters;
    }

    /**
     * Create a folder
     * @param folder
     * @return true if the folder has been created, false otherwise
     */
    public boolean createFolder(String folder) {

        File folderF = new File(folder);
        if ( !folderF.mkdir() ) {
            return false;
        }

       return true;
    }

    /**
     * Copy file
     * @param file source file
     * @param destinationFolder destination folder
     * @throws IOException
     */
    public void copyFile(String file, String destinationFolder) throws IOException{

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
     * Create the job file for the Isami runIsami.sh script
     * @param parameterSet
     */
    public void createJobFile(ParameterSet parameterSet,String... resultFileNames) throws IOException,NullPointerException {
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

        for(String resultFile: resultFileNames) {
            fileContent.append(resultFile).append("\n");
        }

        /**
         * Stes spectre options
         */
        if (getParameters().getParameter("czmFile").getValue().toString().toLowerCase().equals("true")) {
            fileContent.append(jobName).append(".czm\n");
        }

        if (getParameters().getParameter("sigmaFile").getValue().toString().toLowerCase().equals("true")) {
//            fileContent.append(jobName).append(".sigma\n");
        }

        if (getParameters().getParameter("traceFile").getValue().toString().toLowerCase().equals("true")) {
            fileContent.append(jobName).append("_HIST1_N.trace\n");
            fileContent.append(jobName).append("_HIST1_N.trace\n");
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
