package gui.creator;

import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.AircraftParameter;
import core.parameters.parametertypes.BooleanParameter;
import core.parameters.parametertypes.DoubleParameter;
import core.parameters.parametertypes.IntegerParameter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.configuration.JStesConfiguration;
import main.configuration.JStesPreferences;
import main.configuration.JobDefinition;
import org.controlsfx.control.PropertySheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Model class for the creator view
 */
public class CreatorModel {

    List<String> fileNames = new ArrayList<>();
    private ObservableList<String> obsFileNameList = FXCollections.observableList(fileNames);

    List<String> jobTypes = new ArrayList<>();
    private ObservableList<String> obsJobType = FXCollections.observableList(jobTypes);

    List<File> files = new ArrayList<>();

    private List<SimpleItem> paramList = new ArrayList<>();
    private ObservableList<PropertySheet.Item> propertySheetItems = FXCollections.observableArrayList();

    public CreatorModel() {

        JStesPreferences preferences = JStesConfiguration.getPreferences();
        for(String jt: preferences.getJobTypes()) {
            getObsJobType().add(jt);
        }
    }

    public void addFiles(List<File> fileList) {
        for (File f: fileList) {
            obsFileNameList.add(f.getName());
            files.add(f);
        }
    }

    public void loadParameters(String jobType) {

        JStesPreferences preferences = JStesConfiguration.getPreferences();

        propertySheetItems.clear();
        for(JobDefinition jobDefinition: preferences.getJobs()) {
            if (jobDefinition.getType().getLabel().equals(jobType)) {
                for (Parameter p: jobDefinition.getParameters().getParameters()) {
                    if (p.getSource().equals("external")) {
                        propertySheetItems.add(new SimpleItem(p));
                    }
                }

            }
        }

    }

    public ObservableList<String> getObsFileNameList() {
        return obsFileNameList;
    }

    public ObservableList<String> getObsJobType() {
        return obsJobType;
    }

    public ObservableList<PropertySheet.Item> getPropertySheetItems() {
        return propertySheetItems;
    }

    /**
     * Class which holds the parameters for the parameterTable
     */
    private class SimpleItem implements PropertySheet.Item {

        Parameter parameter;

        public SimpleItem(Parameter p) {
            this.parameter = p;
        }

        @Override
        public Class<?> getType() {

            if (parameter instanceof BooleanParameter) {
                return Boolean.class;
            }
            else if(parameter instanceof IntegerParameter) {
                return Integer.class;
            }
            else if (parameter instanceof AircraftParameter) {
                return parameter.getClass();
            }
            else {
                return String.class;
            }
        }

        @Override
        public String getCategory() {
            if (parameter.getCategory() == null) {
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

        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }
    }
}
