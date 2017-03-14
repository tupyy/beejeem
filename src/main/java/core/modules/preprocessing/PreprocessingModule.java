package core.modules.preprocessing;

import com.sshtools.sftp.SftpClient;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshSession;
import core.modules.LocalModule;
import core.modules.Module;
import core.modules.ModuleException;
import core.modules.preprocessing.methods.PreprocessMethod;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PreprocessingModule implements LocalModule {

    private Logger logger = LoggerFactory.getLogger(PreprocessingModule.class);
    private static final String MODULE_NAME = "PreprocessingModule";

    private final List<String> methodsName = new ArrayList<String>(
            Arrays.asList("PreprocessMethod"));

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public ModuleTask runModule(UUID jobID, ParameterSet parameterSet) throws ModuleException {

        //first try to create the PreprocessMethod
        /**
         * Look for the parameters needed by the PreprocessMethod
         */
        //this parameter is internally created and we're sure that exits
        try {
            StringParameter tempFolder = parameterSet.getParameter("temporaryFolder");
            StringParameter inputFile = parameterSet.getParameter("filename");

            if (inputFile.getValue() == null ) {
                throw new ModuleException("Empty value for the input file");

            }

            PreprocessMethod method = new PreprocessMethod(MODULE_NAME,jobID,parameterSet);
            logger.info("Module {} Method {} created for job {}",MODULE_NAME,"PreprocessingFileMethod",jobID);
            return new ModuleTask("PreprocessFileTask",method);
        }
        catch (IllegalArgumentException ex) {
            throw new ModuleException(ex.getMessage());
        }
    }

    @Override
    public List<String> getMethodsName() {
        return methodsName;
    }
}
