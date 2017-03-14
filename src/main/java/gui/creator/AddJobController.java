package gui.creator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tctupangiu on 10/03/2017.
 */

public class AddJobController implements Initializable {

    @FXML
    private Button okButton;

    @FXML
    private Button selectFileButton;

    @FXML
    private ListView listView;

    @FXML
    private Button cancelButton;

     public void initialize(URL location, ResourceBundle resources) {

         cancelButton.setOnAction((event) -> {
             // close the dialog.
             Node  source = (Node)  event.getSource();
             Stage stage  = (Stage) source.getScene().getWindow();
             stage.close();

         });

         selectFileButton.setOnAction((event) -> {
             FileChooser fileChooser = new FileChooser();
             fileChooser.setTitle("Choose input files");
             fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Stf files","stf","dat"));

             Node  source = (Node)  event.getSource();
             Stage stage  = (Stage) source.getScene().getWindow();
             fileChooser.showOpenDialog(stage);
         });
    }
}
