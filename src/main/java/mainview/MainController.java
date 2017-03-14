package mainview;

import addJob.AddJobController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable{

    @FXML
    private Button addJobButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //
        addJobButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage dialog = new Stage();

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    Pane root  = fxmlLoader.load(AddJobController.class.getResource("addJob.fxml"));
                    Scene scene = new Scene(root);
                    AddJobController controller = fxmlLoader.getController();
                    dialog.setScene(scene);
                    dialog.setTitle("Add jobs");
                    dialog.setResizable(false);

                    dialog.initOwner((Stage) addJobButton.getScene().getWindow());
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.showAndWait();
                }
                catch (IOException e) {

                }

            }
        });
    }
}
