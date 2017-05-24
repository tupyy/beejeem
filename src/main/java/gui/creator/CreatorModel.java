package gui.creator;

import configuration.JobDefinition;
import configuration.Preferences;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import gui.propertySheet.PropertyModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import configuration.JStesConfiguration;
import main.JStesCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for the creator view
 */
public class CreatorModel {

    private ObservableList<FileEntry> obsFileNameList = FXCollections.observableArrayList();
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    List<String> jobTypes = new ArrayList<>();
    private ObservableList<String> obsJobType = FXCollections.observableList(jobTypes);


    /**
     * Property sheet model
     */
    private PropertyModel propertyModel = new PropertyModel();
    private JobDefinition currentJobDefition;

    public CreatorModel() {

        Preferences preferences = JStesConfiguration.getPreferences();
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
            obsFileNameList.add(new FileEntry(f));
        }
    }

    public void addFile(File file) {
            obsFileNameList.add(new FileEntry(file));

    }

    public List<File> getFiles() {
        List<File> files = new ArrayList<>();

        for (FileEntry fileEntry: obsFileNameList) {
            files.add(fileEntry.getFile());
        }

        return files;
    }

    public void addFolder(String folderPath) {

        try {
            Files.walk(Paths.get(folderPath))
                    .filter(p -> p.toString().contains("ABRE_"))
                    .forEach((file) ->{
                         addFile(file.toFile());
                    });
        } catch (IOException e) {

        }

    }

    /**
     * Load the parameters of the selected job type
     * @param jobType the type of the job
     */
    public void loadParameters(String jobType) {

        Preferences preferences = JStesConfiguration.getPreferences();
        getPropertyModel().clear();

         for(JobDefinition jobDefinition: preferences.getJobDefinitions()) {
            if (jobDefinition.getType().equals(jobType)) {

                getPropertyModel().setData(addParameterFromPreferences(jobDefinition.getParameters()),null);
                currentJobDefition = jobDefinition;
            }
        }

    }

    public boolean fileRequired() {
        try {
            Parameter fileName = getPropertyModel().getData().getParameter("filename");
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Get the list of the files
     * @return {@code ObservableList<String> containing the list of files}
     */
    public ObservableList<FileEntry> getObsFileNameList() {
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


    public void clear() {
        obsFileNameList.clear();
    }


    /**
     *
     * @param selectedItems
     */
    public void removeFileEntry(ObservableList<Integer> selectedItems) {
        obsFileNameList.removeAll(selectedItems);

    }

    public int countFiles() {
        return obsFileNameList.size();
    }

    public class FileEntry {
        private File file;

        public FileEntry(File file) {
            this.file = file;
        }

        public String getName() {
            return file.getName();
        }

        public File getFile() {
            return file;
        }
    }

    private ParameterSet addParameterFromPreferences(ParameterSet parameters) {
        Preferences preferences = JStesConfiguration.getPreferences();

        try {
            Parameter parameter = parameters.getParameter("localFolder");
            parameter.setValue(preferences.getValue("localFolder"));
        }
        catch (IllegalArgumentException ex) {
            StringParameter localFolder = new StringParameter("localFolder", "Local folder where all the result files are uploaded",
                    "Job", preferences.getValue("localFolder"), "Local folder", "external");
            parameters.addParameter(localFolder);
        }

        try {
            Parameter parameter = parameters.getParameter("destinationFolder");
            parameter.setValue(preferences.getValue("remoteFolder"));
        }
        catch (IllegalArgumentException ex) {
            StringParameter destinationFolder = new StringParameter("destinationFolder", "Remote folder",
                    "Job", preferences.getValue("remoteFolder"), "Remote folder", "external");
            parameters.addParameter(destinationFolder);
        }
        return parameters;
    }
}
