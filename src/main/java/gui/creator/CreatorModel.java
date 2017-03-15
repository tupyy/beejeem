package gui.creator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.configuration.JStesConfiguration;
import main.configuration.JStesPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 15/03/2017.
 */
public class CreatorModel {

    List<String> fileNames = new ArrayList<>();
    private ObservableList<String> obsFileNameList = FXCollections.observableList(fileNames);

    List<String> jobTypes = new ArrayList<>();
    private ObservableList<String> obsJobType = FXCollections.observableList(jobTypes);

    List<File> files = new ArrayList<>();

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


    public ObservableList<String> getObsFileNameList() {
        return obsFileNameList;
    }

    public ObservableList<String> getObsJobType() {
        return obsJobType;
    }
}
