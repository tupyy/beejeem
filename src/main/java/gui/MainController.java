package gui;

import gui.creator.CreatorController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable{

    @FXML
    private Button addJobButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox splitPaneVBox;

    @FXML
    private VBox splitPaneHub;

    @FXML
    private HBox statusBarPane;

    public void initialize(URL location, ResourceBundle resources) {

        showCommandView(splitPaneVBox);
        showHubView(splitPaneHub);
        showStatusBar(statusBarPane);
        //
        addJobButton.setOnAction(event -> {
            Stage dialog = new Stage();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                Pane root  = fxmlLoader.load(MainController.class.getClassLoader().getResource("gui/creator/addJob.fxml"));
                Scene scene = new Scene(root);
                CreatorController controller = fxmlLoader.getController();
                dialog.setScene(scene);
                dialog.setTitle("Add jobs");
                dialog.setResizable(false);

                dialog.initOwner((Stage) addJobButton.getScene().getWindow());
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.showAndWait();
            }
            catch (IOException e) {

            }

        });
    }

    /**
     * Show sidepanel view
     * @param parentNode
     */
    private void showCommandView(VBox parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("sidepanel/sidePanel.fxml"));
            VBox command = (VBox) loader.load();

            parentNode.getChildren().add(command);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add hub view
     * @param parentNode
     */
    private void showHubView(VBox parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("hub/hub.fxml"));
            VBox hubPane = (VBox) loader.load();
            parentNode.getChildren().add(hubPane);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add Detail View
     * @param parentNode
     */
    private void showInfoView(BorderPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("jobDetail/jobDetail.fxml"));
            VBox infoPane = (VBox) loader.load();

            parentNode.setRight(infoPane);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Show the status bar
     * @param parentNode
     */
    private void showStatusBar(HBox parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getClassLoader().getResource("statusBar/statusBar.fxml"));
            HBox statusBar = (HBox) loader.load();

            parentNode.getChildren().add(statusBar);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
