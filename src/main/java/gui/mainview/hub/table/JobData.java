package gui.mainview.hub.table;

import javafx.beans.property.SimpleStringProperty;

/**
 * Class which defines the model for the hub table
 */
public class JobData {

    private final SimpleStringProperty name;
    private final SimpleStringProperty destinationFolder;
    private final SimpleStringProperty type;
    private final SimpleStringProperty aircraft;
    private final SimpleStringProperty status;
    private final SimpleStringProperty id;

    public JobData(String name,String id,String destinationFolder,String type,String aircraft,
                    String status) {
        this.name = new SimpleStringProperty(name);
        this.destinationFolder = new SimpleStringProperty(destinationFolder);
        this.type = new SimpleStringProperty(type);
        this.aircraft = new SimpleStringProperty(aircraft);
        this.status = new SimpleStringProperty(status);
        this.id = new SimpleStringProperty(id);
    }


    public SimpleStringProperty getName() {
        return name;
    }

    public SimpleStringProperty getDestinationFolder() {
        return destinationFolder;
    }

    public SimpleStringProperty getType() {
        return type;
    }

    public SimpleStringProperty getAircraft() {
        return aircraft;
    }

    public SimpleStringProperty getStatus() {
        return status;
    }

    public SimpleStringProperty getId() {
        return id;
    }
}
