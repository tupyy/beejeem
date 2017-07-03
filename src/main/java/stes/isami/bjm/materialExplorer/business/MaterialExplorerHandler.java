package stes.isami.bjm.materialExplorer.business;

import com.google.common.eventbus.EventBus;
import javafx.scene.control.Alert;
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
     * Do import action. Create the import job
     *
     * @param files
     */
    public void doImportAction(List<File> files) {

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

    /**
     * Create the export job.
     * <p>If the job is created, it is executed automatically</p>
     *
     * @param materialList
     */
    public void doExportAction(List<Material> materialList) {
        MaterialJobFactory materialJobFactory = new MaterialJobFactory();
        try {
            String materialListString = createMaterialList(materialList);
            Job importJob = materialJobFactory.createExportJob(materialListString);
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
            stringBuilder.append(material.getMaterialName()).append(",").append(material.getReferenceName()).append("/");
        }

        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

}