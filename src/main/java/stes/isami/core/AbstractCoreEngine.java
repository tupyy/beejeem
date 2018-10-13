package stes.isami.core;

import stes.isami.core.job.JobEvent;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Abstract class for {@link CoreEngine}
 */
public abstract class AbstractCoreEngine implements Core {

    private transient Vector listeners;
    private transient Vector jobListeners;

    public AbstractCoreEngine() {}


    @Override
    synchronized public void addJobListener(JobListener l) {
        if (jobListeners == null) {
            jobListeners = new Vector();
        }
        jobListeners.addElement(l);
    }

    /**
     * Remove a listener for JobEvents
     */
    @Override
    synchronized public void removeJobListener(JobListener l) {
        if (jobListeners == null) {
            return;
        }
        jobListeners.removeElement(l);
    }

    //</editor-fold>

    /**
     * Fire JobEvent to all registered listeners
     */
    protected void fireJobEvent(JobEvent event) {
        // if we have no listeners, do nothing...
        if (jobListeners != null && !jobListeners.isEmpty()) {

            // make a copy of the listener list in case
            //   anyone adds/removes listeners
            Vector targets;
            synchronized (this) {
                targets = (Vector) jobListeners.clone();
            }

            // walk through the listener list and
            //   call the sunMoved methods in each
            Enumeration e = targets.elements();
            while (e.hasMoreElements()) {
                JobListener l = (JobListener) e.nextElement();
                l.onJobEvent(event);
            }
        }
    }
}
