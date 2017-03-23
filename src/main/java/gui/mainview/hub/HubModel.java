package gui.mainview.hub;

import core.SimpleLogger;
import core.job.JobExecutionProgress;
import gui.mainview.hub.table.HubTableModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tctupangiu on 22/03/2017.
 */
public class HubModel {

    //table model
    private HubTableModel tableModel = new HubTableModel();

    Map<UUID, SimpleLogger> loggerMap = new HashMap<>();

    public HubModel() {

    }


    public HubTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Get the logger for the job uuid. If there is non, create one
     * @param uuid
     * @return
     */
    public JobExecutionProgress getJobLogger(UUID uuid) {
        if (loggerMap.containsKey(uuid)) {
            return loggerMap.get(uuid);
        }

        SimpleLogger logger = new SimpleLogger(uuid);
        loggerMap.put(uuid,logger);
        return logger;
    }
}
