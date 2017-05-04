package gui.creator;

import configuration.JStesConfiguration;
import core.creator.Creator;
import core.creator.CreatorFactory;
import core.job.Job;
import core.job.JobException;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import gui.propertySheet.PropertyController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.*;
import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.JStesCore.getCoreEngine;

/**
 * Controller class for the Creator view
 */
public class CreatorController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @FXML
    private Button okButton;

    @FXML
    private Button addFileButton;

    @FXML
    private Button addFolderButton;

    @FXML
    private Button clearFileButton;

    @FXML
    private Button removeFileButton;

    @FXML
    private ListView fileListView;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox jobTypeComboBox;

    @FXML
    private VBox propertyVBox;

    private PropertySheet properySheet;

    private CreatorModel model = new CreatorModel();
    private PropertyController propertyController;

    private String fileSearchPattern = "ABRE_";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        jobTypeComboBox.setItems(model.getObsJobType());

        propertyController = new PropertyController(model.getPropertyModel());
        properySheet = propertyController.getPropertySheet();

        propertyVBox.getChildren().add(properySheet);
        fileListView.setItems(model.getObsFileNameList());

        cancelButton.setOnAction((event) -> {
            // close the dialog.
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();


        });

        fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileListView.setCellFactory(param -> new ListCell<CreatorModel.FileEntry>() {
            @Override
            protected void updateItem(CreatorModel.FileEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        //if the user selects a item in the list enable remove button
        fileListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) (event) -> {
                int selectedIndex = fileListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex != -1) {
                    removeFileButton.setDisable(false);
                }
        });

        setButtonActions();


        jobTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            String newJobType = (String) newValue;
            model.loadParameters(newJobType);

            /**
             * TODO change
             * Active the addFolderButton only for the A400M
             */
            if (newJobType.equals("Spectre stes A400M")) {
                addFolderButton.setDisable(false);
                fileSearchPattern = "ABRE_";
            }
            else if (newJobType.equals("Spectre stes")){
                addFolderButton.setDisable(false);
                fileSearchPattern = "(\\.stf)|(SG_data_file)";
            }
            else  {
                addFolderButton.setDisable(true);
            }

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

    /**
     * Set action handlers for the fileList buttons
     */
    private void setButtonActions() {

        /**
         * Ok button action handler
         */
        okButton.setOnAction((event) -> {

            try {
                //get the type of the job
                Parameter type = model.getPropertyModel().getParameterSet().getParameter("type");
                String jobType = (String) type.getValue();
                Creator creator = CreatorFactory.getCreator(jobType.toLowerCase());
                if ( creator!= null ) {
                    createJob(creator, model.getFiles(), model.getPropertyModel().getParameterSet());
                } else {
                    //parameter type don't exists
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Create job");
                    alert.setHeaderText("Plugin not found");
                    alert.setContentText(String.format("The creator for the job type %s is not found", jobType));
                    alert.show();
                    return;
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

        /**
         * Add file button
         */
        addFileButton.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();


            File initialFolder = new File(JStesConfiguration.getPreferences().getValue("lastVisitedFolder"));

            if ( initialFolder.exists() && initialFolder.isDirectory()) {
                fileChooser.setInitialDirectory(initialFolder);
            }
            fileChooser.setTitle("Choose input files");

            Node  source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            List<File> files = fileChooser.showOpenMultipleDialog(stage);
            if (files != null) {
                model.addFiles(files);
                fileListView.setItems(model.getObsFileNameList());
                clearFileButton.setDisable(false);

                JStesConfiguration.getPreferences().setValue("lastVisitedFolder",lastVisitedFolder(files.get(0)));
                if (jobTypeComboBox.getSelectionModel().getSelectedIndex() > -1) {
                    okButton.setDisable(false);
                }
            }
        });

        addFolderButton.setOnAction((event) -> {
            DirectoryChooser folderChooser = new DirectoryChooser();


            File initialFolder = new File(JStesConfiguration.getPreferences().getValue("lastVisitedFolder"));

            if ( initialFolder.exists() && initialFolder.isDirectory()) {
                folderChooser.setInitialDirectory(initialFolder);
            }
            folderChooser.setTitle("Choose folder");

            Node  source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            File folder = folderChooser.showDialog(stage);
            if (folder != null) {

                Task<List<File>> task = new Task<List<File>>() {

                    @Override
                    protected List<File> call() throws Exception {
                        try {
                            List<File> myList = new ArrayList<File>();
                            Files.walk(Paths.get(folder.getPath()))
                                    .filter( p -> matchesFilter(p.toString(),fileSearchPattern))
                                    .forEach((file) ->{
                                        myList.add(file.toFile());
                                    });
                            return myList;
                        } catch (IOException e) {
                            return new ArrayList<>();
                        }
                    }
                };

                Stage dialog = createDialogBox(folder.getPath());

                task.setOnRunning( e -> {
                    Platform.runLater(() -> {
                      dialog.setOnCloseRequest(ee -> {
                            logger.info("Task is closing");
                            task.cancel();
                        });
                        dialog.showAndWait();
                    });
                });

                task.setOnSucceeded(e -> {
                        dialog.close();
                        List<File> fileList = task.getValue();
                        model.addFiles(fileList);
                    });

                task.setOnFailed((e) -> {
                    dialog.close();
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Add folder");
                    alertError.setHeaderText("Error reading");
                    alertError.setContentText("Error".concat(e.getSource().getException().getMessage()));
                    alertError.show();
                });

                new Thread(task).start();

                clearFileButton.setDisable(false);

                JStesConfiguration.getPreferences().setValue("lastVisitedFolder",folder.getPath());
                if (jobTypeComboBox.getSelectionModel().getSelectedIndex() > -1) {
                    okButton.setDisable(false);
                }
            }
        });

        /**
         * Clear button action handler
         */
        clearFileButton.setOnAction((event) -> {
            model.clear();

            if (model.countFiles() == 0)  {
                removeFileButton.setDisable(true);
                clearFileButton.setDisable(true);
            }
        });

        /**
         * Set on action removeButton
         */
        removeFileButton.setOnAction((event) -> {

             ObservableList<Integer> selectedIndices = FXCollections.observableArrayList( //copy
                     fileListView.getSelectionModel().getSelectedItems());
             model.removeFileEntry(selectedIndices);

            if (model.countFiles() == 0)  {
                removeFileButton.setDisable(true);
                clearFileButton.setDisable(true);
            }

        });

    }

    private String lastVisitedFolder(File file) {
        String path = file.getPath();
        return path.substring(0,path.lastIndexOf("\\"));
    }

    private boolean matchesFilter(String filename,String fileSearchPattern) {
        Pattern pattern = Pattern.compile(fileSearchPattern);
        Matcher m = pattern.matcher(filename);

        return m.find();
    }

    private Stage createDialogBox(String textMessage) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(600);
        dialog.setHeight(100);
        dialog.setResizable(false);

        VBox newGroup = new VBox(new Text(25, 25, "Looking for files in folder:\n".concat(textMessage)));

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(600);


        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefHeight(10);
        progressBar.setPrefWidth(600);
        progressBar.setStyle("-fx-padding: 20px 0 0 0");

        hBox.getChildren().add(progressBar);
        hBox.setHgrow(progressBar, Priority.ALWAYS);
        newGroup.getChildren().add(hBox);

//        cancelButton.setOnAction(e -> {
//            dialog.close();
//        });


        Scene scene = new Scene((Parent) newGroup);
        dialog.setScene(scene);
        return dialog;
    }

}
