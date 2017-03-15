package gui.creator;

import core.creator.Creator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tctupangiu on 15/03/2017.
 */
public class CreatorModel {

    List<String> fileNames = new ArrayList<>();
    private ObservableList<String> observableList = FXCollections.observableList(fileNames);

    List<File> files = new ArrayList<>();

    public CreatorModel() {

    }

    public void addFiles(List<File> fileList) {
        for (File f: fileList) {
            fileNames.add(f.getName());
            files.add(f);
        }
    }


    public ObservableList<String> getObservableList() {
        return observableList;
    }
}
