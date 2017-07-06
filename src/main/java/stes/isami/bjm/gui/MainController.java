package stes.isami.bjm.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.MasterDetailPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.eventbus.AbstractComponentEventHandler;
import stes.isami.bjm.eventbus.JobEvent;
import stes.isami.bjm.eventbus.DefaultJobEvent;
import stes.isami.bjm.eventbus.JobEvent.JobEventType;
import stes.isami.bjm.gui.mainview.hub.HubController;
import stes.isami.bjm.gui.mainview.sidepanel.SidePanelController;
import stes.isami.bjm.main.JStesCore;
import stes.isami.bjm.main.MainApp;
import stes.isami.bjm.materialExplorer.MaterialExplorer;
import stes.isami.core.JobListener;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;


public class MainController extends AbstractComponentEventHandler implements Initializable,JobListener {
    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    @FXML
    private Button addJobButton;

    @FXML
    private Button deleteButton;
    @FXML private Button exportMaterialButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private BorderPane borderHubPane;

    private MasterDetailPane masterDetailPane;

    private VBox detailPane;

    @FXML private ToggleButton showParameterView;

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
        getCoreEngine().addJobListener(this);
    }

    public void initialize(URL location, ResourceBundle resources) {

        masterDetailPane = new MasterDetailPane(Side.LEFT);
        masterDetailPane.setShowDetailNode(true);
        masterDetailPane.setDividerPosition(0.35);

        createHubDetailView(masterDetailPane);
        createHubView(masterDetailPane);
        borderHubPane.setCenter(masterDetailPane);
        showParameterView.selectedProperty().bindBidirectional(masterDetailPane.showDetailNodeProperty());

        showStatusBar(statusBarPane);

        createActions();
        setupMenuAction();
        addJobButton.setOnAction(newJobEventHandler);

        decorateButton(addJobButton,"images/newJob.png");
        addJobButton.setTooltip(new Tooltip("Create job"));
        decorateButton(deleteButton,"images/remove.png");
        deleteButton.setTooltip(new Tooltip("Delete selected jobs"));
        decorateButton(exportMaterialButton,"images/explorer.png");
        exportMaterialButton.setTooltip(new Tooltip("Material Explorer"));

    }

    @Override
    public void jobUpdated(UUID id) {

    }

    @Override
    public void jobCreated(UUID id) {
        deleteButton.setDisable(false);
    }

    @Override
    public void onStateChanged(UUID id, int newState) {

    }

    /**
     * On job event handler
     * @param event
     */
    public void onJobEvent(JobEvent event) {

        switch (event.getEvent()) {
            case DESELECT:
                deleteButton.setDisable(true);
                break;
            case DELETE:
                break;
            case SELECT:
                deleteButton.setDisable(false);
                break;
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
    private void createHubDetailView(MasterDetailPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("views/sidepanel/sidePanel.fxml"));
            VBox detailPane = (VBox) loader.load();

            sidePanelController = loader.getController();
            parentNode.setDetailNode(detailPane);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add hub view
     * @param parentNode
     */
    private void createHubView(MasterDetailPane parentNode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("views/hub.fxml"));
            VBox hubPane = (VBox) loader.load();
            parentNode.setMasterNode(hubPane);
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

            JStesCore.getEventBus().post(new DefaultJobEvent(JobEventType.DELETE));

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

                dialog.initOwner((Stage) borderHubPane.getScene().getWindow());
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setWidth(700);
                dialog.setHeight(520);
                dialog.showAndWait();
            }
            catch (IOException e) {
                logger.error(e.getMessage());
            }
        });

        exportMaterialButton.setOnAction(event -> {

            Stage dialog = new Stage();

            try {
                MaterialExplorer materialExplorer = new MaterialExplorer();
                Scene scene = new Scene(materialExplorer.getRootPane());
                dialog.setScene(scene);
                dialog.initOwner((Stage) borderHubPane.getScene().getWindow());
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Material explorer");
                dialog.setWidth(950);
                dialog.setHeight(700);
                dialog.setResizable(true);
                dialog.showAndWait();
            }
            catch (IOException ex) {
                logger.error(ex.getMessage());
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
