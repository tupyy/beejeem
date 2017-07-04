package stes.isami.bjm.exportMaterial;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tctupangiu on 03/07/2017.
 */
public class ExportDialogController implements Initializable {

    @FXML private Button runJobButton;
    @FXML private Button closeButton;
    @FXML private Button addMaterial;
    @FXML private TextField materialNameTextField;
    @FXML private TextField materielReferenceTextField;
    @FXML private ListView materialList;

    private ObservableList<String> materialData = FXCollections.observableArrayList();
    private ExportDialog parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createButtonActions();
        materialList.setItems(materialData);
    }

    public void setParent(ExportDialog parent) {
        this.parent = parent;
    }

    private void createButtonActions() {
        addMaterial.setOnAction(event -> {
            if (materialNameTextField.getText().isEmpty() || materielReferenceTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Material definition invalid.");
                alert.setContentText("The material should be defined with name and reference");
                alert.show();
            }
            else {
                materialData.add(materialNameTextField.getText()+","+materielReferenceTextField.getText());
                materielReferenceTextField.setText("");
                materialNameTextField.setText("");
            }

        });

        runJobButton.setOnAction(event -> {
            if (parent != null) {
                StringBuilder stringBuilder = new StringBuilder();

                for (String item: materialData) {
                    stringBuilder.append(item);
                    stringBuilder.append("/");
                }
                parent.runExportJob(stringBuilder.toString().substring(0,stringBuilder.toString().length()-1));

                // close the dialog.
                Node source = (Node)  event.getSource();
                Stage stage  = (Stage) source.getScene().getWindow();
                stage.close();
            }
        });
        closeButton.setOnAction((event) -> {
            // close the dialog.
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        });
    }


}
