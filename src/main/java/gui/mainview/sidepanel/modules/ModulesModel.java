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

            getData().add(new SimpleEntry(moduleController.getName(),trigger));
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

        public SimpleEntry(String name,String trigger) {
            this.name = new SimpleStringProperty(formatName(name));
            this.trigger = new SimpleStringProperty(trigger);
        }


        public String getName() {
            return name.get();
        }

        public String getTrigger() {
            return trigger.get();
        }

        private String formatName(String name) {
            return name.substring(name.lastIndexOf(".")+1);
        }

    }
    //</editor-fold>
}
