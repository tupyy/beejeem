package gui.mainview.hub;
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

    public HubModel() {

    }


    public HubTableModel getTableModel() {
        return tableModel;
    }


}
