package stes.isami.bjm.materialExplorer.business;

import com.google.common.eventbus.EventBus;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.materialExplorer.presenter.actions.ButtonAction;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;

import java.io.File;
import java.util.List;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class MaterialExplorerHandler {

    private final EventBus eventBus;

    public MaterialExplorerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Do action
     * @param action
     */
    public void doAction(ButtonAction.Actions action,Stage stage) {
        switch (action) {
            case LOAD_MATERIALS:
                break;
            case IMPORT:
                doImportAction(stage);
                break;
            case EXPORT_TO_EXCEL:
                break;
            case EXPORT_TO_XML:
                break;
            case CLOSE:
                break;
        }
    }

    /********************************************************************
     *
     *                              PRIVATE
     *
     ********************************************************************/

    private void doImportAction(Stage stage) {
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
            MaterialJobFactory materialJobFactory = new MaterialJobFactory();
            try {
                Job importJob = materialJobFactory.createImportJob(files);
                getCoreEngine().addJob(importJob);
            } catch (JobException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error creating the import job");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }

        }
    }
}
