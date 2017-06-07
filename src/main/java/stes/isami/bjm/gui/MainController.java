package stes.isami.bjm.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.eventbus.AbstractComponentEventHandler;
import stes.isami.bjm.eventbus.ComponentAction;
import stes.isami.bjm.eventbus.DefaultComponentAction;
import stes.isami.bjm.eventbus.JobEvent;
import stes.isami.bjm.gui.mainview.hub.HubController;
import stes.isami.bjm.gui.mainview.sidepanel.SidePanelController;
import stes.isami.bjm.main.JStesCore;
import stes.isami.bjm.main.MainApp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController extends AbstractComponentEventHandler implements Initializable {
    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    @FXML
    private Button addJobButton;

    @FXML
    private Button deleteButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox splitPaneVBox;

    @FXML
    private VBox splitPaneHub;

    @FXML
    private HBox statusBarPane;

    @FXML
    private MenuItem quitMenuItem;

    @FXML
    private MenuItem preferencesMenuItem;

    @FXML
    private MenuItem newJobMenuItem;

    @FXML MenuItem aboutMenuItem;

    private SidePanelController sidePanelController;

    private HubController hubController;
    private EventHandler<ActionEvent> newJobEventHandler;

    public MainController() {
        super();
    }

    public void initialize(URL location, ResourceBundle resources) {

        showSidePanelView(splitPaneVBox);
        showHubView(splitPaneHub);
        showStatusBar(statusBarPane);

        createActions();
        setupMenuAction();
        addJobButton.setOnAction(newJobEventHandler);

        decorateButton(addJobButton,"images/newJob.png");
        decorateButton(deleteButton,"images/remove.png");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onJobEvent(JobEvent event) {

        switch (event.getAction()) {
            case JOB_CREATED:
                deleteButton.setDisable(false);
                break;
        }
    }

    public void onComponentAction(ComponentAction event) {
        if (event.getAction() == ComponentAction.ComponentActions.DESELECT) {
            deleteButton.setDisable(true);
        }
        else if (event.getAction() == ComponentAction.ComponentActions.SELECT) {
            deleteButton.setDisable(false);
        }
    }

    /********************************************************************
     *
     *                          P R I V A T E
     *
     ********************************************************************/

    /**
     * Show sidepanel view
     * @param parentNode
     */
    private void showSidePanelView(VBox parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("views/sidepanel/sidePanel.fxml"));
            VBox command = (VBox) loader.load();

            sidePanelController = loader.getController();
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
            loader.setLocation(MainController.class.getClassLoader().getResource("views/hub.fxml"));
            VBox hubPane = (VBox) loader.load();
            parentNode.getChildren().add(hubPane);
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
            loader.setLocation(MainApp.class.getClassLoader().getResource("views/statusBar.fxml"));
            HBox statusBar = (HBox) loader.load();

            parentNode.getChildren().add(statusBar);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setupMenuAction() {
        quitMenuItem.setOnAction((event) -> {
            Stage stage = (Stage) addJobButton.getScene().getWindow();
            stage.close();

        });

        newJobMenuItem.setOnAction(newJobEventHandler);

        aboutMenuItem.setOnAction(event -> {
            Stage aboutDialog = new Stage();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getClassLoader().getResource("views/about.fxml");
                BorderPane root  = fxmlLoader.load(url);
                Scene scene = new Scene(root);

                aboutDialog.setScene(scene);
                aboutDialog.setTitle("About");
                aboutDialog.setResizable(false);

                aboutDialog.initOwner((Stage) addJobButton.getScene().getWindow());
                aboutDialog.initModality(Modality.APPLICATION_MODAL);
                aboutDialog.setWidth(800);
                aboutDialog.setHeight(524);
                aboutDialog.showAndWait();
            }
            catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
    }

    /**
     * Create actions
     */
    private void createActions() {
        newJobEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage dialog = new Stage();

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getClassLoader().getResource("views/creator.fxml");
                    Pane root  = fxmlLoader.load(url);
                    Scene scene = new Scene(root);

                    dialog.setScene(scene);
                    dialog.setTitle("New jobs");
                    dialog.setResizable(false);

                    dialog.initOwner((Stage) addJobButton.getScene().getWindow());
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.showAndWait();
                }
                catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        };

        deleteButton.setOnAction(event -> {

            JStesCore.getEventBus().post(new DefaultComponentAction(ComponentAction.ComponentActions.DELETE));

        });

        preferencesMenuItem.setOnAction(event -> {

            Stage dialog = new Stage();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getClassLoader().getResource("views/preferences.fxml");
                VBox root  = fxmlLoader.load(url);
                Scene scene = new Scene(root);

                dialog.setScene(scene);
                dialog.setTitle("Preferences");
                dialog.setResizable(false);

                dialog.initOwner((Stage) splitPaneVBox.getScene().getWindow());
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setWidth(700);
                dialog.setHeight(520);
                dialog.showAndWait();
            }
            catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

    }


    /**
     * Add icons to buttons
     */
    private void decorateButton(Button button,String imagePath) {
        URL s = HubController.class.getClassLoader().getResource(imagePath);
        ImageView imageView = new ImageView(new Image(s.toString()));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        button.setGraphic(imageView);
    }


}
