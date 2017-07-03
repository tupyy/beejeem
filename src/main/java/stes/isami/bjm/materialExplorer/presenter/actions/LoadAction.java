package stes.isami.bjm.materialExplorer.presenter.actions;

import com.google.common.eventbus.Subscribe;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import stes.isami.bjm.materialExplorer.business.LoadLibraryEvent;
import stes.isami.bjm.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.materialExplorer.presenter.MaterialExplorerController;

/**
 * Handle load material list action
 */
public class LoadAction implements EventHandler<ActionEvent> {

    private final MaterialExplorerHandler handler;
    private final MaterialExplorerController controller;
    private SimpleDoubleProperty loadingProgress = new SimpleDoubleProperty(85);
    private SimpleStringProperty textProgress = new SimpleStringProperty("loading");
    private MaskerPane maskerPane;

    public LoadAction(MaterialExplorerHandler handler, MaterialExplorerController controller) {
        this.handler = handler;
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to load the libray?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            showMasker();
            showProgressbar(controller.getStatusPane());

            handler.register(this);
            handler.doLoadAction();

        }
        else {
            alert.close();
        }
    }

    @Subscribe
    public void onLoadLibraryEvent(LoadLibraryEvent event) {

    }

    /**
     * Show the masker pane
     */
    private void showMasker() {
        maskerPane = new MaskerPane();
        controller.getMainPane().getChildren().add(maskerPane);
    }

    private void showProgressbar(HBox statusBox) {
        ProgressIndicatorBar progressBar = new ProgressIndicatorBar(loadingProgress,100,textProgress);
        progressBar.setPrefHeight(30);
        progressBar.prefWidthProperty().bind(statusBox.widthProperty());
        progressBar.setPadding(new Insets(0,10,0,10));

        Button cancelTaskButton = new Button("Cancel");
        cancelTaskButton.setMinWidth(100);
        cancelTaskButton.setMaxWidth(100);
        cancelTaskButton.setOnAction(event -> {
            maskerPane.setVisible(false);
            handler.stopLoadingJob();
        });

        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setPadding(new Insets(0,30,0,0));
        HBox.setHgrow(statusBox, Priority.ALWAYS);
        statusBox.getChildren().addAll(progressBar,cancelTaskButton);
    }
}
