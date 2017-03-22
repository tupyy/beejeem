package gui.propertySheet;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.AircraftParameter;
import core.parameters.parametertypes.BooleanParameter;
import core.parameters.parametertypes.IntegerParameter;
import core.parameters.parametertypes.ListParameter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public PropertyModel() {
    }


    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    public void setParameterSet(ParameterSet parameterSet) {
        this.parameterSet = parameterSet;
        addItems(parameterSet);
    }

    public void clear() {
        getPropertySheetItems().clear();
    }

    /**
     * Add simple items to observable list
     *
     * @param parameterSet
     */
    private void addItems(ParameterSet parameterSet) {

        for (Parameter p : parameterSet) {
            if (p.getSource().equals("external")) {
                getPropertySheetItems().add(new SimpleItem(p));
            }
        }
    }

    //<editor-fold desc="SimpleItem class">
    public ObservableList<PropertySheet.Item> getPropertySheetItems() {
        return propertySheetItems;
    }

    /**
     * Class which holds the parameters for the parameterTable
     */
    public class SimpleItem implements PropertySheet.Item {

        Parameter parameter;

        public SimpleItem(Parameter p) {
            this.parameter = p;
        }

        @Override
        public Class<?> getType() {

            if (parameter instanceof BooleanParameter) {
                return Boolean.class;
            } else if (parameter instanceof IntegerParameter) {
                return Integer.class;
            } else if (parameter instanceof AircraftParameter) {
                return parameter.getClass();
            } else if (parameter instanceof ListParameter) {
                return List.class;
            } else {
                return String.class;
            }
        }

        @Override
        public String getCategory() {
            if (parameter.getCategory().isEmpty()) {
                return "General";
            }

            return parameter.getCategory();
        }

        @Override
        public String getName() {
            return parameter.getLabel();
        }

        @Override
        public String getDescription() {
            return parameter.getDescription();
        }

        @Override
        public Object getValue() {
            return parameter.getValue();
        }

        @Override
        public void setValue(Object o) {
            parameter.setValue(o);
        }

        public List<String> getOptions() {
            if (parameter instanceof ListParameter) {
                ListParameter listParameter = (ListParameter) parameter;
                return listParameter.getOptions();
            }

            return null;
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }
    }
    //</editor-fold>

}