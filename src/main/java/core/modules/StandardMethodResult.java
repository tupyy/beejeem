package core.modules;

import core.parameters.Parameter;
import core.parameters.ParameterSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tctupangiu on 12/01/2017.
 */
public class StandardMethodResult implements MethodResult {

    private final String methodName;
    private final String moduleName;
    private int value;
    private final List<String> errorMessages = new ArrayList<>();
    private final UUID jobID;
    private ParameterSet parameterSet = new ParameterSet();

    private Boolean lastMethod;

    public StandardMethodResult(String moduleName,String methodName,UUID jobID) {
        this(moduleName,methodName,jobID,0);
    }

    public StandardMethodResult(String moduleName, String methodName, UUID jobID, int value) {
        this.methodName = methodName;
        this.moduleName = moduleName;
        this.value = value;
        this.jobID = jobID;
    }

    public StandardMethodResult(String moduleName,String methodName, UUID jobID, int value, String errorMessage) {
        this(moduleName,methodName, jobID, value);
        errorMessages.add(errorMessage);
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public int getExitCode() {
        return value;
    }

    @Override
    public void setExitCode(int exitCode) {
        this.value = exitCode;
    }

    @Override
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    @Override
    public UUID getJobID() {
        return jobID;
    }

    @Override
    public ParameterSet getResultParameters() {
        return parameterSet;
    }

    @Override
    public Boolean isLast() {
        return lastMethod;
    }

    @Override
    public Class<? extends MethodResult> getMethodResultClass() {
        return this.getClass();
    }

    /**
     * Add a new parameter to the parameterSet
     * @param parameter
     */
    public void addParameter(Parameter<?> parameter) {
        parameterSet.addParameter(parameter);
    }

    protected void setLastMethod(Boolean lastMethod) {
        this.lastMethod = lastMethod;
    }
}
