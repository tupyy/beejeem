import core.modules.LocalModule;
import core.modules.Module;
import core.modules.ModuleException;
import core.parameters.ParameterSet;
import core.tasks.ModuleTask;

import java.util.List;
import java.util.UUID;

/**
 * Created by tctupangiu on 25/04/2017.
 */
public class TestModule implements LocalModule {


    private String name;

    public TestModule(String name) {
        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }


    @Override
    public ModuleTask runModule(UUID jobID, ParameterSet parameterSet) throws ModuleException {
        TestMethod testmethod = new TestMethod("testmethod_"+name);

        ModuleTask moduleTask = new ModuleTask("Task_"+name,testmethod);
        return moduleTask;
    }
}
