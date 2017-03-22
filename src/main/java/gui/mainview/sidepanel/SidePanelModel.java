package gui.mainview.sidepanel;

import core.parameters.ParameterSet;
import gui.propertySheet.PropertyModel;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class SidePanelModel {

    private PropertyModel propertyModel = new PropertyModel();

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
