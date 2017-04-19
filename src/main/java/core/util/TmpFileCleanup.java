package core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by tctupangiu on 24/03/2017.
 */
public class TmpFileCleanup implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        try {
            logger.info("Delete the temporary folders");
            File[] folders = new File(System.getProperty("java.io.tmpdir")).listFiles();
            if (folders != null) {
                for (File folder: folders) {
                    if (folder.isDirectory() && folder.getName().contains("Job_")) {
                        File[] files = folder.listFiles();
                        for (File file: files) {
                            file.delete();
                        }
                        folder.delete();
                    }
                }
            }
        }
        catch (SecurityException ex) {
            logger.error(ex.getMessage());
        }
    }
}
