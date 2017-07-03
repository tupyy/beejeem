package stes.isami.bjm.materialExplorer.business;

import com.google.common.eventbus.EventBus;
import stes.isami.bjm.materialExplorer.presenter.actions.ButtonAction;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class MaterialExplorerHandler {

    private final EventBus eventBus;

    public MaterialExplorerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Do action
     * @param action
     */
    public void doAction(ButtonAction.Actions action) {
        switch (action) {
            case LOAD_MATERIALS:
                break;
            case IMPORT:
                break;
            case EXPORT_TO_EXCEL:
                break;
            case EXPORT_TO_XML:
                break;
            case CLOSE:
                break;
        }
    }

    /********************************************************************
     *
     *                              PRIVATE
     *
     ********************************************************************/
}
