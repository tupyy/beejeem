package stes.isami.bjm.ui.hub.logic;

import javafx.collections.ObservableList;
import stes.isami.bjm.ui.hub.presenter.HubController;

import java.util.List;
import java.util.UUID;

/**
 * Interface of the hub model
 */
public interface HubModel {

    /**
     * Get data
     * @return observable list
     */
    ObservableList<JobData> getData();

    /**
     * Delete a list of jobs
     * @param
     */
    void deleteJobs(List<UUID> jobIDs);

    /**
     * Execute jobs
     * @param jobIDs
     */
    void executeJob(List<UUID> jobIDs);

    /**
     * Execute all available jobs
     */
    void executeAll();

    /**
     * Stop jobs
     * @param jobIDs
     */
    void stopJob(List<UUID> jobIDs);

    /**
     * Action perfomed when the app is shutting down
     */
    void shutdown();

    void setController(HubController controller);


}
