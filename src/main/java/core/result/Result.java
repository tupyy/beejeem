package core.result;

import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This interface represents any component which handle the results of a job.
 * The ValueType is type of the object returned by the getValue method.
 */
public interface Result<ValueType> {

    /**
     * Get the ID
     * @return id
     */
    public UUID getID();

    /**
     * Get the id of the job which generated the result
     * @return id of the job
     */
    public UUID getJobID();

    /**
     * Get the name of the result
     * @return
     */
    public String getName();

    /**
     * Get the value of the parameter
     * @return the an object representing the value of the result
     */
    public ValueType getValue();

    /**
     * Get the content of the html file
     * @return content of the html file
     */
    public Document getHtmlFile();

    /**
     * Get the name of the files attached to the result.
     * <br> In the case of a spectre, there are a number of files which can be present: trace and sigma files
     * @return list of filenames
     */
    public ArrayList<String> getFileNames();

    /**
     * Get the file
     * @param filename
     * @return the file as File class
     */
    public File getFile(String filename);

    /**
     * Return the number of files.
     * <br> The html file is counted.
     * @return the number of files
     */
    public int countFiles();

}
