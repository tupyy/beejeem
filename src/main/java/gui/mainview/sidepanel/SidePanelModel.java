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
    private ModulesModel modulesModel = new ModulesModel();
    private JobInfoModel jobInfoModel = new JobInfoModel();

    private UUID selectedJobID;

    public SidePanelModel() {

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

        modulesModel.populate(job);
        jobInfoModel.populate(job,jobExecutionProgress);
    }


    public ModulesModel getModulesModel() {
        return modulesModel;
    }

    public JobInfoModel getJobInfoModel() {
        return jobInfoModel;
    }

}
