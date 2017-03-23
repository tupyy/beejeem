package gui.mainview.sidepanel.modules;

import core.job.Job;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

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

    private ModulesModel model;

    public ModulesController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public ModulesModel getModel() {
        return model;
    }

    public void setModel(ModulesModel model) {
        this.model = model;
        setupTable();
    }

    private void setupTable() {

        if (model != null) {
            moduleNameColumn.setCellValueFactory(new PropertyValueFactory<ModulesModel.SimpleEntry, String>("name"));
            triggerColumn.setCellValueFactory(new PropertyValueFactory<ModulesModel.SimpleEntry, String>("trigger"));
            modulesTable.setItems(getModel().getData());
        }
    }
}
