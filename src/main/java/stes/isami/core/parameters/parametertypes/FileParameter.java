package stes.isami.core.parameters.parametertypes;

import java.io.File;

/**
 * Class to hold a file name
 */
public class FileParameter extends StringParameter {

    public FileParameter(String name, String description, String category, String defaultValue) {
        super(name, description, category, defaultValue);
    }

    /**
     * Return true if the file exists
     * @return
     */
    public boolean isFileAndExists() {
        File file = new File(getValue());

        return file.exists() && file.isFile();
    }

    /**
     * Get the file. If the file do not exists or it is not a file return {@code null}
     * @return
     */
    public File getFile() {
        File file = new File(getValue());

        if (isFileAndExists()) {
            return file;
        }

        return null;
    }



}
