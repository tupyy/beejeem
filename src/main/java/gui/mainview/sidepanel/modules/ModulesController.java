package gui.mainview.sidepanel.modules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
