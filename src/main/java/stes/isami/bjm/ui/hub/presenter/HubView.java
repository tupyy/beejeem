package stes.isami.bjm.ui.hub.presenter;

import javafx.collections.ObservableList;
import stes.isami.bjm.ui.hub.logic.JobData;

import java.util.List;
import java.util.UUID;


/**
 * Interface to be implemented by the HubViewImpl
 */
public interface HubView  {

    public static int RUN_JOB_ACTION = 1;
    public static int RUN_ALL_ACTION = 2;
    public static int DELETE_ACTION = 3;
    public static int STOP_ACTION = 4;

    /**
     * Called by controller when the deletion action has started in {@link stes.isami.core.CoreEngine}
     */
    public void onStartDeletion();

    /**
     * Called by controller when the deletion has been finished
     */
    public void onEndDeletion();

    /**
     * Set controller
     * @param controller
     */
    public void setController(HubController controller);

    /**
     * Called when the ssh session has been disconnected
     */
    public void onSshDisconnect();

    /**
     * Called when the ssh session has been connected and
     * authenticated
     */
    public void onSshAuthenticated();

    /**
     * Set the data
     * @param data
     */
    public void setData(ObservableList<JobData> data);

    /**
     * Get the list of the selected jobs
     * @return
     */
    public List<UUID> getSelectedJobs();

    public void shutdown();

}
