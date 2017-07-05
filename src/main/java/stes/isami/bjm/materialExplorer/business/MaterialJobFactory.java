package stes.isami.bjm.materialExplorer.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.configuration.JStesConfiguration;
import stes.isami.bjm.configuration.JobDefinition;
import stes.isami.bjm.configuration.Preferences;
import stes.isami.bjm.gui.MainController;
import stes.isami.bjm.main.JStesCore;
import stes.isami.core.creator.Creator;
import stes.isami.core.creator.CreatorFactory;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;
import stes.isami.core.parameters.Parameter;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.parameters.parametertypes.StringParameter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Factory class for the material jobs.
 * <p>It creates the following jobs:</p>
 * <ui>
 *     <li>job for loading the material list from isami</li>
 *     <li>import materials</li>
 *     <li>export materials</li>
 * </ui>
 * <p>To be able to create the jobs, the templates has to be loaded at startup</p>
 */
public class MaterialJobFactory {

    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    public MaterialJobFactory() {

    }

    /**
     * Create the export job.
     * @param materialList list of materials as following: "Materialname_1,Reference_1/Materialname_2,Reference2..
     * @return {@link Job} created
     */
    public Job createExportJob(String materialList,String isamiVersion) throws NullPointerException {

        JobDefinition exportMaterialDefinition;
        List<Job> jobs = null;

        exportMaterialDefinition = JStesConfiguration.getPreferences().getJobDefinition("Export material");

        if (exportMaterialDefinition == null) {
            throw new NullPointerException("Cannot find the template for exporting materials");
        }
        //get the type of the job
        try {

            Creator creator = CreatorFactory.getCreator("basicPlugin.creator.BasicCreator");
            ParameterSet parameters = exportMaterialDefinition.getParameters();
            parameters.getParameter("name").setValue("export_materials");
            parameters.getParameter("materialList").setValue(materialList);
            parameters.getParameter("isamiVersion").setValue(isamiVersion);
            jobs = creator.createJobs(Optional.empty(),addParameterFromPreferences(parameters),exportMaterialDefinition.getModuleElements());

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error(e.getMessage());
        }catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new NullPointerException("Cannot create export job: " + e.getMessage());
        }

        return jobs.get(0);

    }

    /**
     * Create the job for importing xml files to isami
     * @param files list of xml format
     * @return {@link Job} job created
     */
    public Job createImportJob(List<File> files,String isamiVersion) {

        JobDefinition importJobDefinition;
        List<Job> jobs = null;

        importJobDefinition = JStesConfiguration.getPreferences().getJobDefinition("Import material");

        if (importJobDefinition == null) {
            throw new NullPointerException("Cannot find the template for importing materials job");
        }
        //get the type of the job
        try {

            Creator creator = CreatorFactory.getCreator("materialPlugin2.creator.MaterialCreator");
            ParameterSet parameters = importJobDefinition.getParameters();
            parameters.getParameter("name").setValue("import_material");
            parameters.getParameter("isamiVersion").setValue(isamiVersion);
            jobs = creator.createJobs(Optional.of(files),addParameterFromPreferences(parameters),importJobDefinition.getModuleElements());

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error(e.getMessage());
            throw new NullPointerException("Cannot create import job: " + e.getMessage());
        }catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new NullPointerException("Cannot create import job: " + e.getMessage());
        }

        return jobs.get(0);
    }

    /**
     * Create the job for loading the material library
     * @return {@link Job} job created
     */
    public Job createLoadJob(String isamiVersion) {

        JobDefinition importJobDefinition;
        List<Job> jobs = null;

        importJobDefinition = JStesConfiguration.getPreferences().getJobDefinition("Load materials");

        if (importJobDefinition == null) {
            throw new NullPointerException("Cannot find the template for importing materials job");
        }
        //get the type of the job
        try {

            Creator creator = CreatorFactory.getCreator("basicPlugin.creator.BasicCreator");
            ParameterSet parameters = importJobDefinition.getParameters();
            parameters.getParameter("isamiVersion").setValue(isamiVersion);
            jobs = creator.createJobs(Optional.empty(),addParameterFromPreferences(parameters),importJobDefinition.getModuleElements());

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            logger.error(e.getMessage());
            throw new NullPointerException("Cannot create loading job: " + e.getMessage());
        }catch (IOException | IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new NullPointerException("Cannot create loading job: " + e.getMessage());
        }

        return jobs.get(0);
    }
    /**
     * Add the parameters from the Preferences.
     * The parameters to be added are: localFolder and destinationFolder which are global
     * @param parameters
     * @return same parameter set
     */
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
