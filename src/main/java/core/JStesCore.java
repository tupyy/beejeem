package main;

import core.Core;
import core.CoreEngine;
import main.configuration.JStesConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by tctupangiu on 09/03/2017.
 */
public class JStesCore {

    private final static Core coreEngine = CoreEngine.getInstance();

    public JStesCore() {



    }

    /**
     * Get core engine
     * @return coreEngine
     */
    public static Core getCoreEngine() {
        return coreEngine;
    }
}
