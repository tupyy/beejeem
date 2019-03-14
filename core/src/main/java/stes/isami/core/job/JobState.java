package stes.isami.core.job;

import java.util.HashMap;
import java.util.Map;

/**
 * It enumerates the states of a job.
 */
public class JobState {
    /**
     * Job local state
     */
    public static final int READY = 1000;
    public static final int PREPROCESSING = 1001;
    public static final int SUBMITTING =  1002;
    public static final int SUBMITTED = 1003;
    public static final int RESTARTING = 1004;

    /**
     * Batch own status
     */
    public static final int WAITING = 2000;
    public static final int RUN =  2001;
    public static final int DELETION =  2002;
    public static final int RESTARTED = 2003;
    public static final int SUSPENDED = 2004;
    public static final int TRANSFERRING = 2005;

    public static final int THRESHOLD = 2006;
    public static final int HOLD = 2007;
    public static final int UNKNOWN = 2008;
    public static final int DELETED = 2009;
    public static final int DONE = 2010;

    /**
     * Local post processing state
     */
    public static final int POSTPROCESSING = 3000;
    public static final int FINISHED = 3001;

    /**
     * Error and pause status
     */
    public static final int ERROR = 9000;
    public static final int NONE = 90001;

    public static final int STOP = 9002;
    public static final int STOPPING = 9003;

    private static HashMap<Integer,String> statusMap = new HashMap();

    public JobState() {

        statusMap.put(READY,"Ready");
        statusMap.put(SUBMITTING,"Submitting");
        statusMap.put(PREPROCESSING,"Preprocessing");
        statusMap.put(SUBMITTED,"Submitted");
        statusMap.put(WAITING,"Waiting");
        statusMap.put(RUN,"Run");
        statusMap.put(DELETION,"Deletion");
        statusMap.put(DELETED,"Deleted");
        statusMap.put(RESTARTED,"Restarted");
        statusMap.put(SUSPENDED,"Suspended");
        statusMap.put(TRANSFERRING,"Transferring");
        statusMap.put(THRESHOLD,"Threshold");
        statusMap.put(HOLD,"Hold");
        statusMap.put(UNKNOWN,"Unknown");
        statusMap.put(DONE,"Done");
        statusMap.put(POSTPROCESSING,"Postprocessing");
        statusMap.put(FINISHED,"Finished");
        statusMap.put(ERROR,"Error");
        statusMap.put(NONE,"None");
        statusMap.put(STOP,"Stop");
        statusMap.put(STOPPING,"Stopping");
    }

    /**
     * Return the description of a state
     * @param stateCode
     * @return
     */
    public static String toString(int stateCode) {
        if (statusMap.containsKey(stateCode)) {
            return statusMap.get(stateCode);
        }

        return "";
    }

    /**
     * Return the code of the state based on its name.
     * @param statusName
     * @return state code
     */
    public static int getStatusCode(String statusName) {
        for(Map.Entry<Integer,String> entry:statusMap.entrySet()) {
            if (entry.getValue().equals(statusName)) {
                return entry.getKey();
            }
        }

        return NONE;
    }
}
