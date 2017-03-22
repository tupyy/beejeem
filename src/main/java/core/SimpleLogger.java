package core;

import core.creator.CreatorLog;
import core.job.JobExecutionProgress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * It implements a simple logger. This class is used to record the log from different classes.
 */
public class SimpleLogger implements CreatorLog,JobExecutionProgress {

    private ObservableList<String> fatalErrorList = FXCollections.observableArrayList();
    private ObservableList<String> errorList= FXCollections.observableArrayList();
    private ObservableList<String> warningList = FXCollections.observableArrayList();
    private ObservableList<String> debugList = FXCollections.observableArrayList();
    private ObservableList<String> infoList = FXCollections.observableArrayList();

    public SimpleLogger() {}

    @Override
    public void fatalError(String message) {

    }

    @Override
    public void error(String message) {

    }

    @Override
    public void warning(String message) {

    }

    @Override
    public void debug(String message) {

    }

    @Override
    public void info(String message) {

    }

    public ObservableList<String> getFatalErrorList() {
        return fatalErrorList;
    }

    public ObservableList<String> getErrorList() {
        return errorList;
    }

    public ObservableList<String> getWarningList() {
        return warningList;
    }

    public ObservableList<String> getDebugList() {
        return debugList;
    }

    public ObservableList<String> getInfoList() {
        return infoList;
    }
}
