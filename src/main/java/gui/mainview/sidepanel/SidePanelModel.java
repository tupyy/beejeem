package gui.mainview.sidepanel;

import core.parameters.ParameterSet;
import gui.propertySheet.PropertyModel;

import java.util.UUID;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class SidePanelModel {

    private PropertyModel propertyModel = new PropertyModel();
    private UUID selectedJobID;

    public SidePanelModel() {

    }

    /**
     * Get the property model
     * @return
     */
    public PropertyModel getPropertyModel() {
        return propertyModel;
    }

    public void loadJobParameters(ParameterSet parameterSet) {
        getPropertyModel().clear();
        getPropertyModel().setParameterSet(parameterSet);
    }
}
