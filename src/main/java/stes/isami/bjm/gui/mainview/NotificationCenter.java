package stes.isami.bjm.gui.mainview;

import org.controlsfx.control.Notifications;
import stes.isami.bjm.main.JStesCore;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;

import java.util.UUID;

/**
 * Created by tctupangiu on 30/06/2017.
 */
public class NotificationCenter implements JobListener {

    public NotificationCenter() {
        JStesCore.getCoreEngine().addJobListener(this);
    }

    @Override
    public void onJobUpdate(UUID id) {

    }

    @Override
    public void onJobCreate(UUID id) {

    }

    @Override
    public void onStatusChange(UUID id) {
        Job j = JStesCore.getCoreEngine().getJob(id);
        if (j.getState() == JobState.ERROR || j.getState() == JobState.FINISHED) {
            showNotfication(j.getName(),j.getState());
        }
    }

    private void showNotfication(String jobName,int state) {
        Notifications notfication =  Notifications.create()
            .title("Job information");

        if (state == JobState.FINISHED) {
             notfication.text("Job " + jobName + " is finished.");
             notfication.showInformation();
        }
        else if (state == JobState.ERROR) {
            notfication.text("Job " + jobName + " is finished with errors.");
             notfication.showError();
        }
    }
}
