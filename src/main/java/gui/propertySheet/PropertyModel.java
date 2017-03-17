package gui.propertySheet;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 17/03/2017.
 */
public class PropertyModel {

    private ParameterSet parameterSet;
    private List<SimpleItem> simpleItems = new ArrayList<>();
    private ObservableList<PropertySheet.Item> propertySheetItems = FXCollections.observableArrayList();

    public PropertyModel(ParameterSet parameterSet) {
        this.setParameterSet(parameterSet);
        addItems(parameterSet);
    }

    public PropertyModel() {}


    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    public void setParameterSet(ParameterSet parameterSet) {
        this.parameterSet = parameterSet;
        addItems(parameterSet);
    }

    /**
     * Add simple items to observable list
     * @param parameterSet
     */
    private void addItems(ParameterSet parameterSet) {

        for(Parameter p: parameterSet) {
            if (p.getSource().equals("external")) {
                getPropertySheetItems().add(new SimpleItem(p));
            }
        }
    }

    public ObservableList<PropertySheet.Item> getPropertySheetItems() {
        return propertySheetItems;
    }
}
