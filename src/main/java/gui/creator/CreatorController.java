package gui.creator;

import core.creator.Creator;
import core.creator.spectre.SpectreJobCreator;
import core.job.Job;
import core.job.JobException;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.plugin.Plugin;
import gui.propertySheet.PropertyController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.PropertySheet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static core.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 15/03/2017.
 */
public class CreatorController implements Initializable {

    @FXML
    private Button okButton;

    @FXML
    private Button selectFileButton;

    @FXML
    private ListView fileList;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox jobTypeComboBox;

    @FXML
    private VBox propertyVBox;

    private PropertySheet properySheet;

    private CreatorModel model = new CreatorModel();
    private PropertyController propertyController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        jobTypeComboBox.setItems(model.getObsJobType());

        propertyController = new PropertyController(model.getPropertyModel());
        properySheet = propertyController.getPropertySheet();

        propertyVBox.getChildren().add(properySheet);

        cancelButton.setOnAction((event) -> {
            // close the dialog.
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();

        });

        okButton.setOnAction((event) -> {

             try {
                //get the type of the job
                Parameter type = model.getPropertyModel().getParameterSet().getParameter("type");
                String jobType = (String) type.getValue();
                if (type.getValue().equals("stesSpectre")) {
                    SpectreJobCreator creator = new SpectreJobCreator();
                    createJob(creator,model.getFiles(),model.getPropertyModel().getParameterSet());
                }
                else {
                    String pluginClass = (String) model.getPropertyModel().getParameterSet().getParameter("plugin").getValue();
                    if ( ! pluginClass.isEmpty() ) {
                        Plugin plugin = getCoreEngine().getPluginLoader().getPlugin(pluginClass);
                        if (plugin != null) {
                            Creator creator = plugin.getCreator();
                            createJob(creator, model.getFiles(), model.getPropertyModel().getParameterSet());
                        } else {
                            //parameter type don't exists
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Create job");
                            alert.setHeaderText("Plugin not found");
                            alert.setContentText(String.format("The plugin %s is not found", pluginClass));
                            alert.show();
                            return;
                        }
                    }
                    else {
                        //parameter type don't exists
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Create job");
                        alert.setHeaderText("Plugin not found");
                        alert.setContentText(String.format("The plugin class name is empty", pluginClass));
                        alert.show();
                    }
                }
            }
            catch (IllegalArgumentException e) {
                //parameter type don't exists
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Create job");
                alert.setHeaderText("Creation error");
                alert.setContentText("The current job type has no value defined");
                alert.show();
                return;
            }

        });

        selectFileButton.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("D:\\IW"));
            fileChooser.setTitle("Choose input files");

            Node  source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            List<File> files = fileChooser.showOpenMultipleDialog(stage);
            if (files != null) {
                model.addFiles(files);
                fileList.setItems(model.getObsFileNameList());

                if (jobTypeComboBox.getSelectionModel().getSelectedIndex() > -1) {
                    okButton.setDisable(false);
                }
            }
        });

        jobTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String newJobType = (String) newValue;
            model.loadParameters(newJobType);

            if (model.getFiles().size() > 0) {
                okButton.setDisable(false);
            }
        });
    }

    /**
     * Create the jobs
     * @param files
     * @param parameterSet
     */
    private void createJob(Creator creator, List<File> files, ParameterSet parameterSet) {

        CreatorLogger logger = new CreatorLogger();
        try {
            List<Job> jobs = creator.createJobs(files,parameterSet,model.getCurrentJobDefintion().getModuleElements(),logger);
            for (Job j: jobs) {
                try {
                    getCoreEngine().addJob(j);
                } catch (JobException e) {
                    logger.error(e.getMessage());
                }
            }

            Stage stage  = (Stage)this.okButton.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
