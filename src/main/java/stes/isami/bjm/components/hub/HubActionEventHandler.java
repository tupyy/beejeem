package stes.isami.bjm.components.hub;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import stes.isami.bjm.components.hub.table.JobData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Event handler for the runJob button
 */
public class HubActionEventHandler implements EventHandler<ActionEvent>{

    public static final int STOP_ACTION = 1;
    public static final int RUN_ACTION = 2;
    public static final int DELETE_ACTION = 4;
    public static final int EMPTY_ACTION = 3;
    private final TableView tableView;

    private SimpleIntegerProperty actionType;

    public HubActionEventHandler(SimpleIntegerProperty actionType, TableView tableView) {
        this.actionType = actionType;
        this.tableView = tableView;
    }

    @Override
    public void handle(ActionEvent event) {

        if (getActionType() == EMPTY_ACTION) {
            return;
        }


        switch (getActionType()) {
            case STOP_ACTION:
                for (UUID id: getSelectedJobID()) {
                    getCoreEngine().stopJob(id);
                }
                break;
            case RUN_ACTION:
                for (UUID id: getSelectedJobID()) {
                    getCoreEngine().executeJob(id);
                }
                break;
            case DELETE_ACTION:
                getCoreEngine().deleteJobs(getSelectedJobID());
                break;
        }
    }

    public int getActionType() {
        return actionType.get();
    }

    public void setActionType(int actionType) {
        this.actionType.set(actionType);
    }

    public SimpleIntegerProperty getActionProperty() {
        return  actionType;
    }

    private List<UUID> getSelectedJobID() {
        List<UUID> uuidList = new ArrayList<>();

        ObservableList<JobData> selection = tableView.getSelectionModel().getSelectedItems();
        if (selection.size() > -1) {
            for (JobData jobData : selection) {
                uuidList.add(UUID.fromString(jobData.getId()));
            }
        }

        return uuidList;
    }
}
