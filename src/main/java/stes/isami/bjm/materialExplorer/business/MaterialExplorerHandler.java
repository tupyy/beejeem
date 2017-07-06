package stes.isami.bjm.materialExplorer.business;

import com.google.common.eventbus.EventBus;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.materialExplorer.presenter.MaterialExplorerController;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobException;
import stes.isami.core.job.JobState;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;
import static stes.isami.bjm.main.JStesCore.getEventBus;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class MaterialExplorerHandler implements JobListener{

    private static final Logger logger = LoggerFactory
            .getLogger(MaterialExplorerHandler.class);
    private final EventBus eventBus = new EventBus();
    private final MaterialExplorerController controller;

    /**
     * Holds the id of all the created jobs
     */
    private final List<UUID> jobList = new ArrayList<>();
    private Job currentLoadJob;
    private int currentStep = 1;

    public MaterialExplorerHandler(MaterialExplorerController controller) {
        getCoreEngine().addJobListener(this);
        this.controller  = controller;
    }

    /**
     * Do import action. Create the import job
     *
     * @param files
     */
    public void doImportAction(List<File> files,String isamiVersion) throws JobException {

        MaterialJobFactory materialJobFactory = new MaterialJobFactory();
        Job importJob = materialJobFactory.createImportJob(files,isamiVersion);
        jobList.add(importJob.getID());
        getCoreEngine().addJob(importJob);
        getCoreEngine().executeJob(importJob.getID());

    }

    /**
     * Create the export job.
     * <p>If the job is created, it is executed automatically</p>
     *
     * @param materialList
     */
    public void doExportAction(List<Material> materialList,String isamiVersion)throws JobException,NullPointerException {
        MaterialJobFactory materialJobFactory = new MaterialJobFactory();
        String materialListString = createMaterialList(materialList);
        Job importJob = materialJobFactory.createExportJob(materialListString,isamiVersion);

        jobList.add(importJob.getID());
        getCoreEngine().addJob(importJob);
        getCoreEngine().executeJob(importJob.getID());

    }

    /**
     * Create and run the loading materials job
     */
    public void doLoadAction(String isamiVersion) throws JobException,NullPointerException {
            MaterialJobFactory materialJobFactory = new MaterialJobFactory();
            Job loadJob = materialJobFactory.createLoadJob(isamiVersion);
            getCoreEngine().addJob(loadJob);
            getCoreEngine().executeJob(loadJob.getID());

            setCurrentLoadJob(loadJob);
            currentStep = 1;
            eventBus.post(new LoadLibraryEvent("Job created",getProgressValue(currentStep)));

    }


    /**
     * Stop the loading job if any
     */
    public void stopLoadingJob() {
        if (getCurrentLoadJob() != null) {
            deleteJobFromCore(getCurrentLoadJob().getID());
            setCurrentLoadJob(null);
        }
    }

    /**
     * Register eventHandler to eventBus
     * @param eventHandler
     */
    public void register(EventHandler eventHandler) {
        eventBus.register(eventHandler);
    }

    /**
     * Close the handler.
     */
    public void close(boolean deleteJobs) {

        if (deleteJobs) {
            jobList.forEach(this::deleteJobFromCore);
        }

        stopLoadingJob();
    }

    public int countJobs() {
        return jobList.size();
    }
    /********************************************************************
     *
     *                              PRIVATE
     *
     ********************************************************************/

    /**
     * Create the material list as string: "name1,reference1/name2,reference2"
     * @param materials
     * @return "name1,reference1/name2,reference2"
     */
    private String createMaterialList(List<Material> materials) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Material material : materials) {
            stringBuilder.append(material.getLibrary()).append(",").append(material.getId()).append("/");
        }

        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    @Override
    public void onStateChanged(UUID id, int newState) {
        String eventMessage = "";

        if (getCurrentLoadJob() != null) {
            if (id.equals(getCurrentLoadJob().getID())) {
                switch (getCurrentLoadJob().getState()) {
                    case JobState.SUBMITTING:
                    case JobState.SUBMITTED:
                    case JobState.POSTPROCESSING:
                        eventMessage = JobState.toString(getCurrentLoadJob().getState());
                        currentStep++;
                        break;
                    case JobState.FINISHED:
                        currentStep++;
                        eventMessage = "Reading material list file";
                        Path filePath  = Paths.get((String)getCurrentLoadJob().getParameters().getParameter("localFolder").getValue(),"material_list.txt");
                        populateMaterialList(filePath.toFile());
                        break;
                    case JobState.RUN:
                    case JobState.WAITING:
                        if (currentStep < 4) {
                            eventMessage = JobState.toString(getCurrentLoadJob().getState());
                            currentStep++;
                        }
                        break;
                }

               eventBus.post(new LoadLibraryEvent(eventMessage, getProgressValue(currentStep)));
            }
        }
    }

    @Override
    public void jobCreated(UUID id) {

    }

    @Override
    public void jobUpdated(UUID id) {

    }

    private Job getCurrentLoadJob() {
        return currentLoadJob;
    }

    private void setCurrentLoadJob(Job currentLoadJob) {
        this.currentLoadJob = currentLoadJob;
    }

    private int getProgressValue(int currentStepValue) {
       final int MAX_STEP = 7;
       return 100/(MAX_STEP-currentStepValue);
    }

    private void populateMaterialList(File materialListFile) {

        if (materialListFile.exists() && materialListFile.isFile()) {
            CompletableFuture<List<Material>> completableFuture = CompletableFuture.supplyAsync(new ReadMaterialLibraryFile(materialListFile));
            completableFuture.thenAccept(materialList -> {

                ObservableList<Material> data =  controller.getData();
                data.addAll(materialList);

                eventBus.post(new LoadLibraryEvent("Finished", getProgressValue(currentStep)));
                deleteJobFromCore(getCurrentLoadJob().getID());
                setCurrentLoadJob(null);
            });

            completableFuture.exceptionally(e -> {
               Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error reading material list");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return new ArrayList<Material>();
            });
        }
    }

    private void deleteJobFromCore(UUID id) {
        getEventBus().post(id);
    }


}