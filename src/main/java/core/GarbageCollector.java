package core;

import core.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class deletes from batch system all the jobs that have been deleted or stopped.
 */
public final class GarbageCollector {

    /**
     * List holding the batch ids to be deleted
     */
    List<Parameter> deletedJobList = new

    public GarbageCollector() {

    }

    public synchronized void registerJobForDeletion(Parameter batchID) {

    }

    private class JobEntry {

        private final Parameter batchId;
        private int countTries = 2;

        public JobEntry(Parameter batchId) {
            this.batchId = batchId;
        }


    }
}
