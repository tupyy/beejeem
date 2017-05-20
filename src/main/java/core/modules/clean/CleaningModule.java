package core.modules.clean;

import core.modules.LocalModule;
import core.modules.ModuleException;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This module is used by the RESTART state.
 * <p>On entry, it deletes the temporary folder of the job</p>
 */
public class CleaningModule implements LocalModule {

    private Logger logger = LoggerFactory.getLogger(CleaningModule.class);
    private String moduleName = "CleaningModule";

    @Override
    public String getName() {
        return moduleName;
    }

    @Override
    public ModuleTask runModule(UUID jobID, ParameterSet parameterSet) throws ModuleException {
        return new ModuleTask("cleaning",new CleaningMethod(moduleName,jobID,parameterSet));
    }
}
