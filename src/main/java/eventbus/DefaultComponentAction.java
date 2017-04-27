package eventbus;

import java.util.UUID;

/**
 * Created by tctupangiu on 27/04/2017.
 */
public class DefaultComponentAction implements ComponentAction {

    private final ComponentActions action;
    private final ComponentEventHandler source;
    private final UUID jobID;

    public DefaultComponentAction(ComponentEventHandler source, ComponentActions action,UUID jobID) {
        this.action = action;
        this.source = source;
        this.jobID = jobID;
    }
    @Override
    public ComponentActions getAction() {
        return action;
    }

    @Override
    public ComponentEventHandler getSource() {
        return source;
    }

    @Override
    public UUID getJobId() {
        return jobID;
    }
}
