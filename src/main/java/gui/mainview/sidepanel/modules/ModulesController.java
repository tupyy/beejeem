package gui.mainview.sidepanel.modules;

import core.job.Job;
import gui.mainview.hub.table.HubTableModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static core.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class ModulesController implements Initializable{

    @FXML
    private Button addModuleButton;

    @FXML
    private Button removeButtonModule;

    @FXML
    private Button moveupButton;

    @FXML
    private Button movedownButton;

    @FXML
    private TableView modulesTable;
    @FXML
    private TableColumn moduleNameColumn;

    @FXML
    private TableColumn triggerColumn;

    private UUID selectedJobId;

    private ModulesModel model = new ModulesModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        moduleNameColumn.setCellValueFactory(new PropertyValueFactory<ModulesModel.SimpleEntry,String>("name"));
        triggerColumn.setCellValueFactory(new PropertyValueFactory<ModulesModel.SimpleEntry,String>("trigger"));
        modulesTable.setItems(model.getData());
    }

    /**
     * Perfom action when a job has been selected on the hubView
     */
    public void onJobSelected(Job job) {
        selectedJobId = job.getID();

        model.clear();
        model.populate(job);
    }
}
