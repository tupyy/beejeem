package stes.isami.bjm.ui.hub.presenter;

import javafx.collections.ObservableList;
import stes.isami.bjm.ui.hub.logic.JobData;

import java.util.UUID;

/**
 * Interface for the controller
 */
public interface HubController {
    /**
     * Return the data from the model
     * @return data from model
     */
    ObservableList<JobData> getData();

    /**
     * Action performed when a jobs have been selected in the view
     * @param id
     */
    void onJobSelection(UUID id);

    /**
     * Action performed when the user click on one of the buttons of the view
     * @param action
     */
    void onActionPerformed(int action);

    /**
     * Inform the view that the delete action has started by the {@link stes.isami.core.CoreEngine}
     */
    void onStartDelete();

    /**
     * Inform the view that the delete action has ended
     */
    void onEndDelete();
}
