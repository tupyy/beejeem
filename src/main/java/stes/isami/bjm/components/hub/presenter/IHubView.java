package stes.isami.bjm.components.hub.presenter;

import stes.isami.bjm.components.ComponentView;
import stes.isami.bjm.components.hub.logic.JobData;


/**
 * Interface to be implentated by the HubView
 */
public interface IHubView extends ComponentView<JobData> {

    public void onStartDeletion();

    public void onEndDeletion();
}
