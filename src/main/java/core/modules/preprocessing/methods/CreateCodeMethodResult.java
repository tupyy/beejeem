package core.modules.preprocessing.methods;

import core.modules.MethodResult;
import core.modules.StandardMethodResult;

import java.util.UUID;

/**
 * Created by tctupangiu on 12/01/2017.
 */
public class CreateCodeMethodResult extends StandardMethodResult {
    private final String METHOD_NAME = "CreateCodeMethod";

    public CreateCodeMethodResult(String moduleName,UUID jobID) {
        super(moduleName,"CreateCodeMethod", jobID);
    }

    public CreateCodeMethodResult(String moduleName,UUID jobID, int value) {
        super(moduleName,"CreateCodeMethod", jobID,value);
    }

    public CreateCodeMethodResult(String moduleName,UUID jobID,int value, String errorMessage) {
        super(moduleName,"CreateCodeMethod", jobID,value,errorMessage);
    }

    public Class<? extends MethodResult> getMethodResultClass() {
        return this.getClass();
    }
}
