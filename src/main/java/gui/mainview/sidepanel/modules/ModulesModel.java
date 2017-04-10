package gui.mainview.sidepanel.modules;

import core.job.Job;
import core.job.JobState;
import core.job.ModuleController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class ModulesModel {

    private final ModulesController controller;
    private ObservableList<SimpleEntry> data = FXCollections.observableArrayList();

    public ModulesModel(ModulesController controller) {
        this.controller = controller;
    }

    public void populate(Job j) {

        getData().clear();

        List<ModuleController> moduleList = j.getModules();
        for(ModuleController moduleController: moduleList) {

            String trigger = "";
            if (moduleController.getTrigger() != 0) {
                trigger = JobState.toString(moduleController.getTrigger());
            }

            getData().add(new SimpleEntry(moduleController.getName(),trigger,moduleController.getState()));
        }
    }

    public ObservableList<SimpleEntry> getData() {
        return data;
    }

    public void clear() {
        data.clear();
    }


    //<editor-fold desc="Job Data">
    /**
     * Class which defines the model for the hub table
     */
    public class SimpleEntry {

        private SimpleStringProperty name;
        private SimpleStringProperty trigger;
        private SimpleStringProperty status;

        public SimpleEntry(String name,String trigger,int status) {
            this.name = new SimpleStringProperty(formatName(name));
            this.trigger = new SimpleStringProperty(trigger);
            this.status = new SimpleStringProperty(parseState(status));
        }


        public String getName() {
            return name.get();
        }

        public String getTrigger() {
            return trigger.get();
        }

        public String getStatus() {
            return status.get();
        }

        private String formatName(String name) {
            return name.substring(name.lastIndexOf(".")+1);
        }

        private String parseState(int state) {

            switch (state) {
                case 0:
                    return "READY";
                case 1:
                    return "SCHEDULED";
                case 2:
                    return "RUNNING";
                case 3:
                    return "FINISHED";
                case 4:
                    return "FAILED";
                default:
                    return "";
            }
        }


    }
    //</editor-fold>
}
