package mainview.hub;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the hubView
 */
public class HubController implements Initializable {

    @FXML
    private TableView hubTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupTable();
    }

    private void setupTable() {
        TableColumn nameCol = new TableColumn("Name");
        TableColumn destinationCol = new TableColumn("Destination");
        TableColumn typeCol = new TableColumn("Type");
        TableColumn aircraftCol = new TableColumn("Aircraft");
        TableColumn statusCol = new TableColumn("Status");
        TableColumn idCol = new TableColumn("ID");

        hubTable.getColumns().addAll(nameCol,destinationCol,typeCol,aircraftCol,statusCol,idCol);

    }
}
