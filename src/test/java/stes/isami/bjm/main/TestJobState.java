package stes.isami.bjm.main;

import javafx.beans.property.SimpleBooleanProperty;
import org.junit.BeforeClass;
import org.junit.Test;
import stes.isami.bjm.ui.hub.logic.JobData;
import stes.isami.bjm.ui.hub.presenter.JobStateTask;
import stes.isami.core.job.Job;
import stes.isami.core.job.JobState;
import stes.isami.core.parameters.ParameterSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for {@link stes.isami.bjm.ui.hub.presenter.JobStateTask}
 */
public class TestJobState {

    JobStateTask jobStateTask;
    static List<JobData> selectionList = new ArrayList<>();

    @BeforeClass
    public static void testSetup() {
        JobState jobState = new JobState();
        for (int i = 0; i < 5 ; i++) {
            selectionList.add(new JobData(createJob(JobState.RUN)));
        }

        selectionList.add(new JobData(createJob(JobState.STOP)));
    }

    @Test
    public void testRunState() {
        SimpleBooleanProperty disableRunButtonProperty = new SimpleBooleanProperty(true);
        SimpleBooleanProperty disableStopButtonProperty = new SimpleBooleanProperty(true);
        JobStateTask task = new JobStateTask(disableRunButtonProperty,disableStopButtonProperty);
        task.setSelectionList(selectionList);

//        task.run();

        assert disableRunButtonProperty.get() == true : "Run job property failed";
        assert  disableStopButtonProperty.get() == true : "Stop property failed";


    }
    private static Job createJob(int state) {
        TestJob job = new TestJob(new ParameterSet(),state);
        return job;
    }

}
