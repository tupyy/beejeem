package stes.isami.bjm.components.materialExplorer.presenter.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.components.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.components.materialExplorer.presenter.MaterialExplorerController;
import stes.isami.core.job.JobException;

import java.io.File;
import java.util.List;

/**
 * Handle import action.
 *
 */
public class ImportAction implements EventHandler<ActionEvent> {

     private final MaterialExplorerHandler handler;
    private final MaterialExplorerController controller;

    public ImportAction(MaterialExplorerHandler handler, MaterialExplorerController controller) {
        this.handler = handler;
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose the XML files to import");
        File initialFolder = new File(JStesConfiguration.getPreferences().getValue("lastVisitedFolder"));

        if ( initialFolder.exists() && initialFolder.isDirectory()) {
            fileChooser.setInitialDirectory(initialFolder);
        }

        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            try {
                handler.doImportAction(files,controller.getIsamiVersion());
            }
            catch (JobException ex) {

            }
            catch (NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error creating the import job");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }

        }
    }

}
