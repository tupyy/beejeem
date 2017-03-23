package gui.mainview.sidepanel;

import core.job.Job;
import core.job.JobExecutionProgress;
import gui.mainview.sidepanel.info.JobInfoModel;
import gui.mainview.sidepanel.modules.ModulesModel;
import gui.propertySheet.PropertyModel;

import java.util.UUID;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class SidePanelModel {

    private PropertyModel propertyModel = new PropertyModel();
    private ModulesModel modulesModel;
    private JobInfoModel jobInfoModel;

    private SidePanelController sidePanelController;

    private UUID selectedJobID;

    public SidePanelModel(SidePanelController sidePanelController) {
        this.sidePanelController = sidePanelController;
    }

    /**
     * Get the property model
     * @return
     */
    public PropertyModel getPropertyModel() {
        return propertyModel;
    }

    public void onJobSelected(Job job, JobExecutionProgress jobExecutionProgress) {

        getPropertyModel().clear();
        getPropertyModel().setParameterSet(job.getParameters());

        getModulesModel().populate(job);
        getJobInfoModel().populate(job,jobExecutionProgress);
    }


    public ModulesModel getModulesModel() {
        return modulesModel;
    }

    public JobInfoModel getJobInfoModel() {
        return jobInfoModel;
    }

    public void setSidePanelController(SidePanelController sidePanelController) {
        this.sidePanelController = sidePanelController;
    }

    public SidePanelController getSidePanelController() {
        return sidePanelController;
    }

    public void setPropertyModel(PropertyModel propertyModel) {
        this.propertyModel = propertyModel;
    }

    public void setModulesModel(ModulesModel modulesModel) {
        this.modulesModel = modulesModel;
    }

    public void setJobInfoModel(JobInfoModel jobInfoModel) {
        this.jobInfoModel = jobInfoModel;
    }
}
