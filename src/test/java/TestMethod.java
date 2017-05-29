import stes.isami.core.modules.Method;
import stes.isami.core.modules.MethodResult;
import stes.isami.core.modules.StandardMethodResult;
import stes.isami.core.parameters.parametertypes.IntegerParameter;
import stes.isami.core.parameters.parametertypes.StringParameter;

import java.util.Random;
import java.util.UUID;

/**
 * Created by tctupangiu on 25/04/2017.
 */
public class TestMethod implements Method {

    private final String name;

    public TestMethod(String methodName) {
        this.name = methodName;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public MethodResult execute() {

        Random random = new Random();
        int i = random.nextInt(5000);
        StandardMethodResult standardMethodResult = new StandardMethodResult("testModule",name, UUID.randomUUID());

        StringParameter batchIDParameter = new StringParameter("batchID","Descriot","cat","938880");
        standardMethodResult.addParameter(batchIDParameter);
        standardMethodResult.setExitCode(0);

        return standardMethodResult;
    }

    @Override
    public void cancel() {

    }
}
