package core;

import core.creator.CreatorLog;
import core.job.JobExecutionProgress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.UUID;

/**
 * It implements a simple logger. This class is used to record the log from different classes.
 */
public class SimpleLogger implements CreatorLog,JobExecutionProgress {

    private final UUID jobId;
    private ObservableList<String> fatalErrorList = FXCollections.observableArrayList();
    private ObservableList<String> errorList= FXCollections.observableArrayList();
    private ObservableList<String> warningList = FXCollections.observableArrayList();
    private ObservableList<String> debugList = FXCollections.observableArrayList();
    private ObservableList<String> infoList = FXCollections.observableArrayList();

    public SimpleLogger(UUID jobId) {
        this.jobId = jobId;
    }

    public SimpleLogger() {
        jobId = UUID.randomUUID();
    }

    @Override
    public void fatalError(String message) {
        getFatalErrorList().add(message);
    }

    @Override
    public void error(String message) {
        getErrorList().add(message);
    }

    @Override
    public void warning(String message) {
        getWarningList().add(message);
    }

    @Override
    public void debug(String message) {
        getDebugList().add(message);
    }

    @Override
    public void info(String message) {
        getInfoList().add(message);
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

    public UUID getJobId() {
        return jobId;
    }
}
