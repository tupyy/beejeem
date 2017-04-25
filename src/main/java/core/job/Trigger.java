package core.job;

/**
 * Created by cosmin on 21/04/2017.
 */
public class Trigger {

    /**
     * Default triggers
     */
    public static final int doPreprocessing = 1000;
    public static final int doSubmit =  1001;
    public static final int doProcessing = 1002;
    public static final int doPostprocessing = 1005;
    public static final int doFinish = 1006;
    public static final int doStop = 1007;
    public static final int doRestart = 1008;
    public static final int doError = 9000;

    /**
     * Batch system events
     */
    public static final int evRunning = 2000;
    public static final int evWainting = 2001;
    public static final int evDeletion =  2002;
    public static final int evRestarted = 2003;
    public static final int evSuspended = 2004;
    public static final int evTransferring = 2005;
    public static final int evError = 2007;
    public static final int evHold = 2008;
    public static final int evDone = 2006;


}
