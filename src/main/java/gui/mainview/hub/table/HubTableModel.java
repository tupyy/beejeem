package gui.mainview.hub.table;

import core.job.Job;
import core.job.JobState;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.AircraftParameter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class HubTableModel {

    private ObservableList<JobData> data = FXCollections.observableArrayList();

    public HubTableModel() {

    }

    public void addJob(Job j) {
        getData().add(new JobData(j));
    }

    public void updateJob(Job j) {

        for(JobData jobData: getData()) {
            if (jobData.getId().equals(j.getID().toString())) {
                jobData.updateJob(j);
            }
        }
    }

    public ObservableList<JobData> getData() {
        return data;
    }


    //<editor-fold desc="Job Data">
    /**
     * Class which defines the model for the hub table
     */
    public class JobData {

        private SimpleStringProperty name;
        private SimpleStringProperty destinationFolder;
        private SimpleStringProperty type;
        private SimpleStringProperty aircraft;
        private SimpleStringProperty status;
        private SimpleStringProperty id;

        public JobData(Job job) {

            try {
                ParameterSet parameterSet = job.getParameters();
                this.name = new SimpleStringProperty((String) parameterSet.getParameter("name").getValue());
                this.destinationFolder = new SimpleStringProperty((String) parameterSet.getParameter("destinationFolder").getValue());
                this.type = new SimpleStringProperty((String) parameterSet.getParameter("type").getValue());

                AircraftParameter aircraftParameter = parameterSet.getParameter("aircraft");
                this.aircraft = new SimpleStringProperty(aircraftParameter.getValue().toString());
                this.status = new SimpleStringProperty(JobState.toString(job.getStatus()));
                this.id = new SimpleStringProperty(job.getID().toString());
            }
            catch (IllegalArgumentException ex) {

            }
        }

        public void updateJob(Job j) {
            this.destinationFolder.set((String) j.getParameters().getParameter("destinationFolder").getValue());
            this.status.set(JobState.toString(j.getStatus()));
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

        public String getAircraft() {
            return aircraft.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getId() {
            return id.get();
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleStringProperty destinationFolderProperty() {
            return destinationFolder;
        }


    }
    //</editor-fold>

}
