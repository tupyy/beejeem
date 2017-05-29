package stes.isami.core.modules;

import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.tasks.ModuleTask;

import java.util.UUID;

/**
 * Created by tctupangiu on 01/03/2017.
 */
public interface LocalModule extends Module {

    /**
     * Run this module with given parameters. The module may create new Tasks
     * and add them to the 'tasks' collection. The module is not supposed to
     * submit the tasks to the TaskController by itself.
     *
     * @param jobID
     * @param parameterSet
     * @return ModuleTask the task which will execute the module's method
     * @throws ModuleException
     */
    public ModuleTask runModule(UUID jobID, ParameterSet parameterSet) throws ModuleException;
}
