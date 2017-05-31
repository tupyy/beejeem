package stes.isami.bjm.configuration;

import stes.isami.bjm.eventbus.*;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import stes.isami.bjm.eventbus.CoreEvent;
import stes.isami.bjm.main.JStesCore;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Preferences view controller
 */
public final class PreferenceController extends AbstractComponentEventHandler implements Initializable {

    //<editor-fold desc="Injections">
    @FXML private GridPane gridPane;
    @FXML private TextField host;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Button selectLocalFolderButton;
    @FXML private TextField localFolder;
    @FXML private TextField remoteFolder;
    @FXML private TextField pluginFolder;
    @FXML private Button selectPluginFolderButton;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private CheckBox autoJobRun;
    //</editor-fold>

    private Scene scene;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";


    private ValidationSupport validationSupport = new ValidationSupport();
    private ValidationDecoration iconDecorator = new GraphicValidationDecoration();

    public PreferenceController() {
        validationSupport.setValidationDecorator(iconDecorator);
        validationSupport.initInitialDecoration();
    }


    public void setScene(Scene scene) { this.scene = scene; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        /**
//         * Create validators
//         */
        createRegexValidator(host,"IP incorrect format",IPADDRESS_PATTERN);
        createFolderValidator(localFolder,"Folder do not exists");
        createFolderValidator(pluginFolder,"Folder do not exists");
        createEmptyValidator(username,"Username must be set");
        createEmptyValidator(password,"Password must be set");
        createEmptyValidator(remoteFolder,"Remote folder must be set");

        setupActions();
        bindProperties();
    }

    /**
     * Create a regex validator and attached to the control
     * @param c
     * @param message
     * @param regex
     */
    private void createRegexValidator(Control c, String message, String regex) {
        validationSupport.registerValidator(c, Validator.createRegexValidator(message,regex, Severity.ERROR));
    }

    /**
     * Create validator for the folders.
     * @param c
     * @param message
     */
    private void createFolderValidator(Control c,String message) {
        validationSupport.registerValidator(c, (Control control,String value) ->
                ValidationResult.fromErrorIf(control,message,!isFolderValid(value)));
    }

    /**
     * Create an empty validator
     * @param c
     * @param message
     */
    private void createEmptyValidator(Control c, String message) {
        validationSupport.registerValidator(c,Validator.createEmptyValidator(message));
    }

    /**
     * Check if a folder path is valid
     * @param folderPath
     * @return
     */
    private boolean isFolderValid(String folderPath) {
       File f = new File(folderPath);
       if (f.isDirectory()) {
           return true;
       }

       return false;
    }

    private void setupActions() {
        okButton.setDefaultButton(true);
        okButton.setOnAction(event -> {
           onOkAction(false);
           closeWindow();
        });

        saveButton.setOnAction(event -> {
            onOkAction(true);
            saveButton.setDisable(true);
        });

        selectLocalFolderButton.setOnAction(new MyEventHandler(localFolder));
        selectPluginFolderButton.setOnAction(new MyEventHandler(pluginFolder));

        cancelButton.setOnAction(event -> {
             closeWindow();
        });
    }

    /**
     * On ok action
     * @param saveFile
     */
    private void onOkAction(boolean saveFile) {

        if (validationSupport.isInvalid()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid input data");
            alert.setContentText("Some textfields containts invalid data. Please correct the values");
            alert.show();
        }
        else {
            JStesCore.getEventBus().post(new DefaultCoreEvent(CoreEvent.CoreEventType.PREFERENCES_UPDATED));

            if (saveFile) {
                JStesCore.getEventBus().post(new DefaultComponentAction(this,ComponentAction.ComponentActions.PREFERENCES_SAVED, UUID.randomUUID()));
            }
        }

    }
    /**
     * Read the values of the parameters from {@link JStesPreferences}
     */
    private void bindProperties() {

        Preferences preferences = JStesConfiguration.getPreferences();

        for (Node n : getNodesOfType(gridPane, TextField.class)) {
            String propertyName = n.getId();
            if (!propertyName.isEmpty()) {
                Property property = preferences.getProperty(propertyName);
                if (property != null) {
                    TextField textField = (TextField) n;
                    textField.setText((String) property.getValue());
                    textField.textProperty().addListener(new TextEventListener());
                    property.bindBidirectional(textField.textProperty());
                }
            }
        }

        //bind checkboxes
        for (Node n : getNodesOfType(gridPane, CheckBox.class)) {
            String propertyName = n.getId();
            if (!propertyName.isEmpty()) {
                Property property = preferences.getProperty(propertyName);
                if (property != null) {
                    CheckBox checkBox = (CheckBox) n;
                    checkBox.setSelected((Boolean) property.getValue());
                    checkBox.selectedProperty().addListener(new CheckboxEventListener());
                    property.bindBidirectional(checkBox.selectedProperty());
                }
            }
        }
    }




    private void closeWindow() {
        Stage stage  = (Stage) okButton.getScene().getWindow();
        stage.close();

    }

    private <T> List<T> getNodesOfType(Pane parent, Class<T> type) {
        List<T> elements = new ArrayList<>();
        for (Node node : parent.getChildren()) {
            if (node instanceof Pane) {
                elements.addAll(getNodesOfType((Pane) node, type));
            } else if (type.isAssignableFrom(node.getClass())) {
                //noinspection unchecked
                elements.add((T) node);
            }
        }
        return Collections.unmodifiableList(elements);
    }

    private class MyEventHandler implements EventHandler<ActionEvent> {

        private final TextField textField;

        public MyEventHandler(TextField textField) {
            this.textField = textField;
        }

        @Override
        public void handle(ActionEvent event) {
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();

            DirectoryChooser folderChooser = new DirectoryChooser();
            File initalFolder = new File(textField.getText());
            if (initalFolder.isDirectory()) {
                folderChooser.setInitialDirectory(initalFolder);
            }

            folderChooser.setTitle("Choose folder");
            File folder = folderChooser.showDialog(stage);
            if (folder != null) {
                textField.setText(folder.getAbsolutePath());
            }
        }
    }

    private class TextEventListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            saveButton.setDisable(false);
        }
    }

    private class CheckboxEventListener implements ChangeListener<Boolean> {


        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            saveButton.setDisable(false);
        }
    }

}
