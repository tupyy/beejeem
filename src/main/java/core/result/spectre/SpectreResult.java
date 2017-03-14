package core.result.spectre;

import core.result.Result;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class represents the result for the spectre jobs.
 */
public class SpectreResult implements Result<ArrayList<String>> {

    //<editor-fold desc="Result interface">
    @Override
    public UUID getID() {
        return null;
    }

    @Override
    public UUID getJobID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ArrayList<String> getValue() {
        return null;
    }


    @Override
    public Document getHtmlFile() {
        return null;
    }

    @Override
    public ArrayList<String> getFileNames() {
        return null;
    }

    @Override
    public File getFile(String filename) {
        return null;
    }

    @Override
    public int countFiles() {
        return 0;
    }

    //</editor-fold>

}
