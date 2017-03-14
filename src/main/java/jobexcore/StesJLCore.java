package jobexcore;

import core.Core;
import core.CoreEngine;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class StesJLCore {

    private final static Core coreEngine = CoreEngine.getInstance();

    public StesJLCore() {

    }

    /**
     * Get core engine
     * @return coreEngine
     */
    public static Core getCoreEngine() {
        return coreEngine;
    }
}
