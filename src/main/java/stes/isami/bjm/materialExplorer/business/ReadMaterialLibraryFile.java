package stes.isami.bjm.materialExplorer.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.bjm.gui.MainController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class reads the content fo the material_list.txt file and creates a list
 * of {@link Material} object.
 */
public class ReadMaterialLibraryFile implements Supplier<List<Material>> {
    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    private final File file;

    public ReadMaterialLibraryFile(File file) {
        this.file = file;
    }

    @Override
    public List<Material> get() {
        List<Material> materials = new ArrayList<>();
        FileReader fileReader = null;
        BufferedReader reader = null;

        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

            String line;
            Material material;
            while ( (line = reader.readLine()) != null ) {
                material = createMaterial(line);
                if (material.isValid()) {
                    materials.add(material);
                }

            }
            fileReader.close();
            reader.close();
        } catch (FileNotFoundException e) {
            logger.error("Input not found: " + e.getMessage());
        }
        catch (IOException ex) {
            logger.error("Input cannot be read: " + ex.getMessage());
        }
        finally {
            try {
                if (fileReader != null & reader != null) {
                    fileReader.close();
                    reader.close();
                }
            }
            catch (IOException ex) {

            }

            return materials;
        }
    }

    /**
     * Create the {@link Material} from the line
     * @param line
     * @return empty material if line is in wrong format
     */
    private Material createMaterial(String line) {
        Material material;
        String[] buff = line.split(" ");

        try {
            if (buff.length == 4) {
                material = new Material(buff[2], buff[0], buff[1], buff[3]);
            } else {
                //no reference for the material
                material = new Material(buff[1], buff[0], "", buff[2]);
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return new Material();
        }

        return material;
    }
}
