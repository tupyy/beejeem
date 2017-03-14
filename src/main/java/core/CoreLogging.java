/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package core;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * MZmine logging support
 */
public final class CoreLogging {

    /**
     * Configures the logging properties according to the logging.properties
     * file found in the jar resources
     */
    public static void configureLogging() {

        try {
            ClassLoader classLoader = CoreLogging.class.getClassLoader();

            FileInputStream fis = new FileInputStream(classLoader.getResource("logging.properties").getFile());
            LogManager logMan = LogManager.getLogManager();
            logMan.readConfiguration(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
