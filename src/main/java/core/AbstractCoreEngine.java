package core;

import core.Core;
import core.CoreEvent;
import core.CoreListener;
import core.JobListener;

import java.util.Enumeration;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by cosmin on 22/04/2017.
 */
public abstract class AbstractCoreEngine implements Core {

    private transient Vector listeners;
    private transient Vector jobListeners;

    public AbstractCoreEngine() {}

    /**
     * Register a listener for JobEvents
     */
    @Override
    synchronized public void addCoreEventListener(CoreListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }

    /**
     * Remove a listener for JobEvents
     */
    @Override
    synchronized public void removeCoreEventListener(CoreListener l) {
        if (listeners == null) {
            return;
        }
        listeners.removeElement(l);
    }

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
    protected void fireJobEvent(JobEvent action, UUID id) {
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
                switch (action) {
                    case JOB_UPDATED:
                        l.jobUpdated(id);
                }
            }
        }
    }

    protected void fireCoreEvent(CoreEvent action) {
        // if we have no listeners, do nothing...
        if (listeners != null && !listeners.isEmpty()) {

            // make a copy of the listener list in case
            //   anyone adds/removes listeners
            Vector targets;
            synchronized (this) {
                targets = (Vector) listeners.clone();
            }

            // walk through the listener list and
            //   call the sunMoved methods in each
            Enumeration e = targets.elements();
            while (e.hasMoreElements()) {
                CoreListener l = (CoreListener) e.nextElement();
                switch (action) {
                    case SSH_CONNECTION_ERROR:
                        l.connectionEvent(CoreEvent.SSH_CONNECTION_ERROR);
                        break;
                }
            }
        }
    }





}
