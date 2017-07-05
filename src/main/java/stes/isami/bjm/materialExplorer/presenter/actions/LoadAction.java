package stes.isami.bjm.materialExplorer.presenter.actions;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.MaskerPane;
import stes.isami.bjm.materialExplorer.business.LoadLibraryEvent;
import stes.isami.bjm.materialExplorer.business.MaterialExplorerHandler;
import stes.isami.bjm.materialExplorer.presenter.MaterialExplorerController;
import stes.isami.core.job.JobException;

/**
 * Handle load material list action
 */
public class LoadAction implements EventHandler<ActionEvent> {

    private final MaterialExplorerHandler handler;
    private final MaterialExplorerController controller;
    private SimpleDoubleProperty loadingProgress = new SimpleDoubleProperty();
    private SimpleStringProperty textProgress = new SimpleStringProperty();
    private MaskerPane maskerPane;

    public LoadAction(MaterialExplorerHandler handler, MaterialExplorerController controller) {
        this.handler = handler;
        this.controller = controller;
        handler.register(this);
    }

    @Override
    public void handle(ActionEvent event) {

        if (controller.getData().size() > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to load the libray?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                controller.clearData();
                startAction();
            } else {
                alert.close();
            }
        }
        else {
            startAction();
        }
    }

    @Subscribe
    public void onLoadLibraryEvent(LoadLibraryEvent event) {
        loadingProgress.set(event.getProgress());
        textProgress.set(event.getMessage());
        if (event.getMessage().equalsIgnoreCase("finished")) {
            hideProgressbar(controller.getStatusPane());
        }
    }

    private void startAction() {
        try {
            handler.doLoadAction(controller.getIsamiVersion());
            controller.setDisableButton(true);
            showProgressbar(controller.getStatusPane());
        }
        catch (JobException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error creating the load job");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Add the progress bar and cancel button
     * @param statusBox
     */
    private void showProgressbar(HBox statusBox) {
        ProgressIndicatorBar progressBar = new ProgressIndicatorBar(loadingProgress,100,textProgress);
        progressBar.setPrefHeight(30);
        progressBar.prefWidthProperty().bind(statusBox.widthProperty());
        progressBar.setPadding(new Insets(0,10,0,10));

        Button cancelTaskButton = new Button("Cancel");
        cancelTaskButton.setMinWidth(100);
        cancelTaskButton.setMaxWidth(100);
        cancelTaskButton.setOnAction(event -> {
            hideProgressbar(statusBox);
            handler.stopLoadingJob();
        });

        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setPadding(new Insets(0,30,0,0));
        HBox.setHgrow(statusBox, Priority.ALWAYS);
        statusBox.getChildren().addAll(progressBar,cancelTaskButton);
    }

    private void hideProgressbar(HBox statusBox) {
        Platform.runLater(() -> {
            controller.setDisableButton(false);
            statusBox.getChildren().clear();
        });

    }
}
