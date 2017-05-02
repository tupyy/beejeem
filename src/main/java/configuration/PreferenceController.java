package configuration;

import eventbus.*;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.JStesCore;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;
import org.controlsfx.validation.decoration.ValidationDecoration;

import java.io.File;
import java.net.URL;
import java.util.*;

import static eventbus.CoreEvent.CoreEventType.PREFERENCES_UPDATED;

/**
 * Preferences view controller
 */
public final class PreferenceController implements Initializable, ComponentEventHandler {

    //<editor-fold desc="Injections">
    @FXML private GridPane gridPane;
    @FXML private TextField hostTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button selectLocalFolderButton;
    @FXML private TextField localFolderTextField;
    @FXML private TextField remoteFolderTextField;
    @FXML private TextField pluginFolderTextField;
    @FXML private Button selectPluginFolderButton;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
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
        JStesCore.registerController(this);
        validationSupport.setValidationDecorator(iconDecorator);
        validationSupport.initInitialDecoration();
    }


    public void setScene(Scene scene) { this.scene = scene; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        /**
//         * Create validators
//         */
        createRegexValidator(hostTextField,"IP incorrect format",IPADDRESS_PATTERN);
        createFolderValidator(localFolderTextField,"Folder do not exists");
        createFolderValidator(pluginFolderTextField,"Folder do not exists");
        createEmptyValidator(usernameTextField,"Username must be set");
        createEmptyValidator(passwordTextField,"Password must be set");
        createEmptyValidator(remoteFolderTextField,"Remote folder must be set");

        setupActions();
        bindProperties();
    }


    @Override
    public void onJobEvent(JobEvent event) {

    }

    @Override
    public void onComponentAction(ComponentAction event) {

    }

    @Override
    public void onCoreEvent(CoreEvent event) {

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

        selectLocalFolderButton.setOnAction(new MyEventHandler(localFolderTextField));
        selectPluginFolderButton.setOnAction(new MyEventHandler(pluginFolderTextField));

        cancelButton.setOnAction(event -> {
             closeWindow();
        });
    }

    private void onOkAction(boolean saveFile) {

        if (validationSupport.isInvalid()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid input data");
            alert.setContentText("Some textfields containts invalid data. Please correct the values");
            alert.show();
        }
        else {
            JStesCore.getEventBus().post(new DefaultCoreEvent(PREFERENCES_UPDATED));

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

        for (Node n: getNodesOfType(gridPane,TextField.class)) {
            String propertyName = n.getId().substring(0,n.getId().lastIndexOf("TextField"));
            if ( !propertyName.isEmpty() ) {
                Property property = preferences.getProperty(propertyName);
                if (property != null) {
                    TextField textField = (TextField) n;
                    textField.setText((String) property.getValue());
                    textField.textProperty().addListener(new TextEventHandler());
                    property.bindBidirectional(textField.textProperty());
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

    private class TextEventHandler implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            saveButton.setDisable(false);
        }
    }

}
