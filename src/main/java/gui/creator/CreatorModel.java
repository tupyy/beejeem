package gui.creator;

import gui.propertySheet.PropertyModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import core.configuration.JStesConfiguration;
import core.configuration.JStesPreferences;
import core.configuration.JobDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for the creator view
 */
public class CreatorModel {

    List<String> fileNames = new ArrayList<>();
    private ObservableList<String> obsFileNameList = FXCollections.observableList(fileNames);

    List<String> jobTypes = new ArrayList<>();
    private ObservableList<String> obsJobType = FXCollections.observableList(jobTypes);

    List<File> files = new ArrayList<>();

    /**
     * Property sheet model
     */
    private PropertyModel propertyModel = new PropertyModel();
    private JobDefinition currentJobDefition;

    public CreatorModel() {

        JStesPreferences preferences = JStesConfiguration.getPreferences();
        for(String jt: preferences.getJobTypes()) {
            getObsJobType().add(jt);
        }
    }

    public JobDefinition getCurrentJobDefintion() {
        return currentJobDefition;
    }

    /**
     * Add files to fileListView
     * @param fileList
     */
    public void addFiles(List<File> fileList) {
        for (File f: fileList) {
            obsFileNameList.add(f.getName());
            files.add(f);
        }
    }

    public List<File> getFiles() {
        return files;
    }

    /**
     * Load the parameters of the selected job type
     * @param jobType the type of the job
     */
    public void loadParameters(String jobType) {

        JStesPreferences preferences = JStesConfiguration.getPreferences();
        getPropertyModel().clear();

         for(JobDefinition jobDefinition: preferences.getJobs()) {
            if (jobDefinition.getType().getLabel().equals(jobType)) {
                getPropertyModel().setParameterSet(jobDefinition.getParameters());
                currentJobDefition = jobDefinition;
            }
        }

    }

    /**
     * Get the list of the files
     * @return {@code ObservableList<String> containing the list of files}
     */
    public ObservableList<String> getObsFileNameList() {
        return obsFileNameList;
    }

    /**
     * Get the list of the files
     * @return {@code ObservableList<String> containing the list of job types}
     */
    public ObservableList<String> getObsJobType() {
        return obsJobType;
    }

    /**
     * Get the propertySheet model
     * @return the model of the property sheet
     */
    public PropertyModel getPropertyModel() {
        return propertyModel;
    }
}