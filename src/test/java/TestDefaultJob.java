import stes.isami.core.job.*;
import stes.isami.core.modules.Module;
import stes.isami.core.modules.StandardMethodResult;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.core.parameters.parametertypes.StringParameter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Test default job
 */
public class TestDefaultJob implements Observer {
    private final Logger logger = LoggerFactory.getLogger(AbstractJob.class);
    private  DefaultJob job;
    private boolean stateChanged = false;

    public TestDefaultJob() {
        JobState jobState = new JobState();
    }

    public void myFirstTest() {

        ParameterSet parameters = new ParameterSet();
        HashMap<Integer,Module> moduleHashMap = new HashMap<>();
        moduleHashMap.put(JobState.SUBMITTING,new TestModule("Submitting"));
        moduleHashMap.put(JobState.STOP,new TestModule("Cleaning"));

        job = new DefaultJob(parameters,moduleHashMap);
        job.addObserver(this);


        try {
            job.execute();
        } catch (JobException e) {
            assert (false);
        }

        while (!stateChanged) {

        }

        stateChanged = false;
        logger.info("Qstat: RUN");
        job.setQstatResult(testQStat("r"));

        while (!stateChanged) {

        }

        stateChanged = false;
        logger.info("QStat: WAITING");
        job.setQstatResult(testQStat("qw"));

        while (!stateChanged) {

        }


    }

    @Override
    public void update(Observable o, Object arg) {

        Integer i = (Integer) arg;
        logger.info("************* New state: {} {} *****************",i,JobState.toString(i));
        stateChanged = true;
    }

    private StandardMethodResult testQStat(String status) {

        //job submitted
        String qstatOutput = "slots ja-task-ID \n" +
                "-----------------------------------------------------------------------------------------------------------------\n" +
                " 938880 0.60000 LR-54-51-5 xizac        " +status+"      04/25/2017 13:05:47 isami_noon.q@aec-afr-cal-05        1      ";
        StandardMethodResult outputResult = new StandardMethodResult("QStatModule","Qstatmethid", UUID.randomUUID());
        StringParameter qstat = new StringParameter("qstatOutput","qstat","cat",qstatOutput);
        outputResult.addParameter(qstat);

        return outputResult;
    }
}
