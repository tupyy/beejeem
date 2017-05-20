package core.modules.clean;

import core.modules.Method;
import core.modules.MethodResult;
import core.modules.StandardMethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

/**
 * Delete the temporary folder of the job
 */
public class CleaningMethod implements Method {

    private Logger logger = LoggerFactory.getLogger(CleaningMethod.class);

    private String methodName = "CleaningMethod";
    private final UUID jobID;
    private final String moduleName;
    private ParameterSet parameters;

    /**
     * @param moduleName the name of the module
     * @param parameterSet the set of parameters of the job
     */
    public CleaningMethod(String moduleName,UUID jobID,ParameterSet parameterSet) {

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

        try {
            Parameter temporaryFolder = parameters.getParameter("temporaryFolder");

            logger.info("Delete the temporary folder {}",temporaryFolder.getValue().toString());
            File tempFolder = new File(temporaryFolder.getValue().toString());
            File[] files = tempFolder.listFiles();
            for (File file: files) {
                file.delete();
            }
            tempFolder.delete();
        }
        catch (SecurityException ex) {
            logger.error(ex.getMessage());
            return new StandardMethodResult(moduleName,methodName,jobID,1,ex.getMessage());
        }

        return new StandardMethodResult(moduleName,methodName,jobID);
    }

    @Override
    public void cancel() {

    }
}
