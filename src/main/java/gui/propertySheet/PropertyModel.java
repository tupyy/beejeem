package gui.propertySheet;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.AircraftParameter;
import core.parameters.parametertypes.BooleanParameter;
import core.parameters.parametertypes.IntegerParameter;
import core.parameters.parametertypes.ListParameter;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tctupangiu on 17/03/2017.
 */
public class PropertyModel {

    private ParameterSet parameterSet;
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

        Platform.runLater(() -> {
            getPropertySheetItems().clear();
            addItems(parameterSet);
        });

    }

    public void updateParameterSet(ParameterSet newParameterSet) {
        Platform.runLater(() -> {

            try {
                //check for new values
                for (Parameter p : newParameterSet) {
                    if (p.getSource().equals("external")) {
                        SimpleItem simpleItem = getItem(p.getID());
                        if (simpleItem != null) {
                            if (!simpleItem.getValue().equals(p.getValue())) {
                                simpleItem.setValue(p.getValue());
                            }
                        } else {
                            addItem(p, parameterSet.isEditable());
                        }
                    }
                }

                //update the editable
                if (parameterSet.isEditable() != newParameterSet.isEditable()) {
                    setEditable(newParameterSet.isEditable());
                }
            }
            catch (NullPointerException ex) {}
        });


    }

    public void clear() {
        Platform.runLater(() -> {
            getPropertySheetItems().clear();
        });
    }

    /**
     * Add simple items to observable list
     *
     * @param parameterSet
     */
    private void addItems(ParameterSet parameterSet) {

        for (Parameter p : parameterSet) {
            if (p.getSource().equals("external")) {
                getPropertySheetItems().add( new SimpleItem(p,parameterSet.isEditable()));
            }
        }


    }

    private void addItem(Parameter p,boolean editable) {
        getPropertySheetItems().add( new SimpleItem(p,editable));
    }
    /**
     * Get the item based on the id
     * @param id
     * @return
     */
    private SimpleItem getItem(UUID id) {
        for(PropertySheet.Item item: getPropertySheetItems()) {
            SimpleItem simpleItem = (SimpleItem) item;
            if (simpleItem.getID().equals(id)) {
                return simpleItem;
            }
        }

        return null;
    }

    /**
     * Set editable
     * @param editable
     */
    private void setEditable(boolean editable) {
        parameterSet.setEditable(editable);

        for(PropertySheet.Item item: getPropertySheetItems()) {
            SimpleItem simpleItem = (SimpleItem) item;
            simpleItem.setEditable(editable);
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
        private boolean editable = true;

        public SimpleItem(Parameter p,boolean editable) {
            this.parameter = p;
            this.editable = editable;
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
        public boolean isEditable() {
            return editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public UUID getID() {
            return parameter.getID();
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }
    }
    //</editor-fold>

}