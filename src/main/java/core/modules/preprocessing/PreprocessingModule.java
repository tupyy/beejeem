package core.modules.preprocessing;

import core.modules.LocalModule;
import core.modules.ModuleException;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class PreprocessingModule implements LocalModule {

    private Logger logger = LoggerFactory.getLogger(PreprocessingModule.class);
    private String moduleName = "PreprocessingModule";
    private List<String> methodNames = new ArrayList<>();

    public PreprocessingModule(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getName() {
        return moduleName;
    }

    @Override
    public ModuleTask runModule(UUID jobID, ParameterSet parameterSet) throws ModuleException {
      return null;
    }

    @Override
    public List<String> getMethodsName() {
        return methodNames;
    }

    public void addMethod(String methodName) {
        methodNames.add(methodName);
    }
}
