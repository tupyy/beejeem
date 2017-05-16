package gui.propertySheet;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.AircraftParameter;
import core.parameters.parametertypes.BooleanParameter;
import core.parameters.parametertypes.IntegerParameter;
import core.parameters.parametertypes.ListParameter;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;

import java.util.*;

/**
 * This class wraps the {@link org.controlsfx.control.PropertySheet.Item} with the values from the
 * {@link ParameterSet}.
 */
public class PropertyModel {

    private PropertyController controller;
    private ParameterSet parameterSet;
    private ObservableList<PropertySheet.Item> propertySheetItems = FXCollections.observableArrayList();

    public PropertyModel() {

    }


    public void addListener(ChangeListener changeListener) {
        for(PropertySheet.Item item: getPropertySheetItems()) {
            SimpleItem simpleItem = (SimpleItem) item;
            simpleItem.getObservableValue().get().addListener(changeListener);
        }
    }
    /**
     * Get data
     * @return {@link ParameterSet} contained by the model
     */
    public ParameterSet getData() {
        return parameterSet;
    }

    /**
     * Set data
     * @param parameterSet
     */
    public void setData(ParameterSet parameterSet,ChangeListener changeListener) {
        this.parameterSet = parameterSet;

        Platform.runLater(() -> {
            getPropertySheetItems().clear();
            addItems(parameterSet);
            if (changeListener != null) {
                addListener(changeListener);
            }
        });


    }

    /**
     * Add new parameter to set
     * @param newParameter
     */
    public void addParameter(Parameter newParameter) {
        this.parameterSet.addParameter(newParameter);
    }

    /**
     * Add a new set of parameters
     * @param parameters
     */
    public void addParameterSet(ParameterSet parameters) {
        this.parameterSet.addParameters(parameters);
    }
    /**
     * Update the data with new values. If new parameters are present in the set, they will be added to the model
     * @param newParameterSet
     */
    public void updateData(ParameterSet newParameterSet) {
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

    /**
     * Clear the data
     */
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
               addItem(p,parameterSet.isEditable());
            }
        }


    }

    private void addItem(Parameter p,boolean editable) {
        SimpleItem simpleItem = new SimpleItem(p,editable);
        getPropertySheetItems().add(simpleItem);
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

    public ObservableList<PropertySheet.Item> getPropertySheetItems() {
        return propertySheetItems;
    }
    //<editor-fold desc="SimpleItem class">

    /**
     * Class which holds the parameters for the parameterTable
     */
    public class SimpleItem implements PropertySheet.Item {

        Parameter parameter;
        private boolean editable = true;
        private boolean isEditing = false;
        private SimpleObjectProperty observableValue;

        public SimpleItem(Parameter p,boolean editable) {
            this.parameter = p;
            this.editable = editable;

            createObservableValue(parameter);
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
            return observableValue.getValue();
        }

        @Override
        public void setValue(Object o) {

            if (!isEditing) {
                isEditing = true;
            }
            parameter.setValue(o);
            observableValue.set(o);
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
            return Optional.ofNullable(observableValue);
        }

        public Parameter getParameter() {
            return parameter;
        }

        private void createObservableValue(Parameter parameter) {
            observableValue = new SimpleObjectProperty(parameter.getName(), parameter.getName(), parameter.getValue());
        }

    }
    //</editor-fold>

}