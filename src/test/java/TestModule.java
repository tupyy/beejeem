import stes.isami.core.modules.LocalModule;
import stes.isami.core.modules.Module;
import stes.isami.core.modules.ModuleException;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.tasks.ModuleTask;

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
