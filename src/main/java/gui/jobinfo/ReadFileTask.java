package gui.jobinfo;

import java.io.*;
import java.util.function.Supplier;

/**
 * Created by tctupangiu on 17/05/2017.
 */
public class ReadFileTask implements Supplier<String> {

    private final File file;

    public ReadFileTask(File file) {
        this.file = file;
    }

    @Override
    public String get() {
        StringBuilder fileContent = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ( (line = reader.readLine()) != null ) {
                    fileContent.append(line +"\n");
                }

                return fileContent.toString();

        } catch (FileNotFoundException e) {
          return "";
        }
        catch (IOException ex) {
            return "";
        }
    }
}
