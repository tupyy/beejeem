package stes.isami.bjm.notifications;

import javafx.application.Platform;
import org.controlsfx.control.Notifications;
import stes.isami.core.JobListener;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;

import java.util.UUID;

import static stes.isami.bjm.main.JStesCore.getCoreEngine;

/**
 * Shows notifications when job are finished
 */
public class NotificationCenter implements JobListener{

    public NotificationCenter() {

    }

    @Override
    public void jobUpdated(UUID id) {

    }

    @Override
    public void jobCreated(UUID id) {

    }

    @Override
    public void onStateChanged(UUID id, int newState) {

        Platform.runLater(() -> {
            Job j = getCoreEngine().getJob(id);

            if (newState == JobState.ERROR) {
                Notifications.create()
                        .text("Job " + j.getName() + " has ended with error.\\n Check the batch output.")
                        .title("Job " + j.getName())
                        .showError();
            }
            else if (newState == JobState.FINISHED) {
                Notifications.create()
                        .text("Job " + j.getName() + " has finished")
                        .title("Job " + j.getName())
                        .showInformation();
            }
        });


    }
}
