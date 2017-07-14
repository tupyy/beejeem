package stes.isami.bjm.main;

import com.google.common.eventbus.EventBus;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;
import stes.isami.core.modules.MethodResult;
import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;

import java.util.UUID;

/**
 * Created by cosmin2 on 14/07/2017.
 */
public class TestJob implements Job {

    private final ParameterSet parameters;
    private int state;
    private UUID id = UUID.randomUUID();

    public TestJob(ParameterSet parameters,int state) {
        this.parameters = parameters;
        this.state = state;
    }

    @Override
    public void execute() throws JobException {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public ParameterSet getParameters() {
        return parameters;
    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    @Override
    public boolean updateParameter(Parameter<?> newParameter) throws JobException {
        return false;
    }

    @Override
    public boolean updateParameter(String parameterName, Object parameterValue) throws IllegalArgumentException, JobException {
        return false;
    }

    @Override
    public boolean updateParametes(ParameterSet parameters) throws JobException {
        return false;
    }

    @Override
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void setQstatResult(MethodResult qstatOutput) {

    }

    @Override
    public void setEventBus(EventBus coreEventBus) {

    }
}
