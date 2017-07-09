package stes.isami.bjm.components.hub.table;

import javafx.beans.property.SimpleStringProperty;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;
import stes.isami.core.parameters.ParameterSet;

/**
 * Model class for the HubView
 */
public class JobData {

    private SimpleStringProperty batchID;
    private SimpleStringProperty name;
    private SimpleStringProperty destinationFolder;
    private SimpleStringProperty type;
    private SimpleStringProperty localFolder;
    private SimpleStringProperty status;
    private SimpleStringProperty id;

    public JobData(Job job) {

        try {
            ParameterSet parameterSet = job.getParameters();
            this.name = new SimpleStringProperty(getParameterValue(parameterSet,"name"));
            this.destinationFolder = new SimpleStringProperty(getParameterValue(parameterSet,"destinationFolder"));

            /**
             * Fix
             */

            try {
                this.type = new SimpleStringProperty(parameterSet.getParameter("type").getDescription());
            }
            catch (IllegalArgumentException ex) {
                this.type = new SimpleStringProperty("Unknown");
            }

            this.localFolder = new SimpleStringProperty(getParameterValue(parameterSet,"localFolder"));
            this.batchID = new SimpleStringProperty(getParameterValue(parameterSet,"batchID"));
            this.status = new SimpleStringProperty(JobState.toString(job.getState()));
            this.id = new SimpleStringProperty(job.getID().toString());
        }
        catch (IllegalArgumentException ex) {

        }
    }

    public void updateJob(Job j) {
        this.name.set((String) j.getParameters().getParameter("name").getValue());
        this.destinationFolder.set((String) j.getParameters().getParameter("destinationFolder").getValue());
        this.localFolder.set((String) j.getParameters().getParameter("localFolder").getValue());
        this.status.set(JobState.toString(j.getState()));
        this.batchID.set(getParameterValue(j.getParameters(),"batchID"));
    }


    public String getName() {
        return name.get();
    }

    public String getDestinationFolder() {
        return destinationFolder.get();
    }

    public String getType() {
        return type.get();
    }

    public String getLocalFolder() {
        return localFolder.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
    public SimpleStringProperty destinationFolderProperty() {
        return destinationFolder;
    }

    public SimpleStringProperty batchIDProperty() {
        return batchID;
    }
    public SimpleStringProperty statusProperty() { return status;};
    public SimpleStringProperty localFolderProperty() {return localFolder;}

    private String getParameterValue(ParameterSet parameters, String parameterName) {
        try {
            return parameters.getParameter(parameterName).getValue().toString();
        }
        catch (IllegalArgumentException ex) {
            return "";
        }
    }

}
