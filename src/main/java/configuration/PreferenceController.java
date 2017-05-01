package configuration;

import eventbus.ComponentAction;
import eventbus.ComponentEventHandler;
import eventbus.CoreEvent;
import eventbus.JobEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.ResourceBundle;

/**
 * Preferences view controller
 */
public final class PreferenceController implements Initializable, ComponentEventHandler {

    //<editor-fold desc="Injections">
    @FXML private VBox preferencePane;
    @FXML private TextField hostTextField;
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button selectLocalFolderButton;
    @FXML private TextField localFolderTextField;
    @FXML private TextField remoteFolderTextField;
    @FXML private TextField pluginFolderTextField;
    @FXML private Button selectPluginFolderButton;
    @FXML private TextField templateFolderTextField;
    @FXML private Button selectTemplateFolderButton;
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
        createFolderValidator(templateFolderTextField,"Folder do not exists");
        createEmptyValidator(usernameTextField,"Username must be set");
        createEmptyValidator(passwordTextField,"Password must be set");
        createEmptyValidator(remoteFolderTextField,"Remote folder must be set");

        setupActions();
        initValues();
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
            if (validationSupport.isInvalid()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Invalid input data");
                alert.setContentText("Some textfields containts invalid data. Please correct the values");
                alert.show();
            }
        });

        selectLocalFolderButton.setOnAction(new MyEventHandler(localFolderTextField));
        selectPluginFolderButton.setOnAction(new MyEventHandler(pluginFolderTextField));
        selectTemplateFolderButton.setOnAction(new MyEventHandler(templateFolderTextField));
    }

    /**
     * Read the values of the parameters from {@link JStesPreferences}
     */
    private void initValues() {

        JStesPreferences preferences = JStesConfiguration.getPreferences();
        hostTextField.setText(preferences.getValue("host"));
        usernameTextField.setText(preferences.getValue("username"));
        passwordTextField.setText(preferences.getValue("password"));
        pluginFolderTextField.setText(preferences.getValue("plugins_folder"));

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
            folderChooser.setTitle("Choose folder");
            File folder = folderChooser.showDialog(stage);
            textField.setText(folder.getAbsolutePath());
        }
    }

}
