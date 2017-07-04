package stes.isami.bjm.exportMaterial;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.configuration.JStesPreferences;
import stes.isami.bjm.configuration.JobDefinition;
import stes.isami.bjm.configuration.Preferences;
import stes.isami.bjm.main.JStesCore;
import stes.isami.core.creator.Creator;
import stes.isami.core.creator.CreatorFactory;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;
import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.parameters.parametertypes.StringParameter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by tctupangiu on 03/07/2017.
 */
public class ExportDialog {
    private static final Logger logger = LoggerFactory
            .getLogger(ExportDialog.class);
    public ExportDialog() {

    }

    public void runExportJob(String materialList) {
        JobDefinition exportMaterialDefinition;

        exportMaterialDefinition = JStesConfiguration.getPreferences().getJobDefinition("Export material");

        //get the type of the job
        try {
            Creator creator = CreatorFactory.getCreator("basicPlugin.creator.BasicCreator");
            List<Job> jobs = null;
            try {

                ParameterSet parameters = exportMaterialDefinition.getParameters();
                parameters.getParameter("name").setValue("export_materials");
                parameters.getParameter("materialList").setValue(materialList);

                jobs = creator.createJobs(Optional.empty(),addParameterFromPreferences(parameters),exportMaterialDefinition.getModuleElements());
                for(Job job: jobs) {
                    JStesCore.getCoreEngine().addJob(job);
                    JStesCore.getCoreEngine().executeJob(job.getID());
                }
            } catch (IOException | JobException e) {
                logger.error(e.getMessage());
            } catch (IllegalArgumentException ex) {
                logger.error(ex.getMessage());
            }


        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * Create the root pane
     * @return
     */
    public Pane getRootPane() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ExportDialog.class.getClassLoader().getResource("views/exportDialog.fxml"));
            Pane pane = (Pane) loader.load();
            ExportDialogController exportDialogController = (ExportDialogController) loader.getController();
            exportDialogController.setParent(this);
            return pane;
        }
        catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    private ParameterSet addParameterFromPreferences(ParameterSet parameters) {
        Preferences preferences = JStesConfiguration.getPreferences();

        try {
            Parameter parameter = parameters.getParameter("localFolder");
            parameter.setValue(preferences.getValue("localFolder"));
        }
        catch (IllegalArgumentException ex) {
            StringParameter localFolder = new StringParameter("localFolder", "Local folder where all the result files are uploaded",
                    "Job", preferences.getValue("localFolder"), "Local folder", "external");
            parameters.addParameter(localFolder);
        }

        try {
            Parameter parameter = parameters.getParameter("destinationFolder");
            parameter.setValue(preferences.getValue("remoteFolder"));
        }
        catch (IllegalArgumentException ex) {
            StringParameter destinationFolder = new StringParameter("destinationFolder", "Remote folder",
                    "Job", preferences.getValue("remoteFolder"), "Remote folder", "external");
            parameters.addParameter(destinationFolder);
        }
        return parameters;
    }


}
