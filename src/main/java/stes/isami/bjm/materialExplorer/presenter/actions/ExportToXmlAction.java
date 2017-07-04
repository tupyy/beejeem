package stes.isami.bjm.materialExplorer.presenter.actions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import stes.isami.bjm.materialExplorer.MaterialExplorer;
import stes.isami.bjm.materialExplorer.business.Material;
import stes.isami.bjm.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.materialExplorer.presenter.MaterialExplorerController;

import java.util.ArrayList;
import java.util.List;

/**
 * Export to XML Action
 */
public class ExportToXmlAction implements EventHandler<ActionEvent> {

    private final MaterialExplorerHandler handler;
    private final MaterialExplorerController controller;

    public ExportToXmlAction(MaterialExplorerHandler handler, MaterialExplorerController controller) {
        this.handler = handler;
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {

        //copy the data
        ObservableList<Material> data = FXCollections.observableArrayList(controller.getData());
        List<Material> materialToExport= new ArrayList<>();

        for (Material material: data) {
            if (material.isSelected()) {
                materialToExport.add(material);
            }
        }

        if (materialToExport.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Export materials failed");
            alert.setContentText("Please select materials to export");
            alert.showAndWait();
        }
        else {
            handler.doExportAction(materialToExport);
        }

    }
}
