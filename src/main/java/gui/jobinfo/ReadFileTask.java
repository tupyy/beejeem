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
        FileReader fileReader = null;
        BufferedReader reader = null;

        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

                String line;
                while ( (line = reader.readLine()) != null ) {
                    fileContent.append(line +"\n");
                }

                fileReader.close();
                reader.close();
                return fileContent.toString();

        } catch (FileNotFoundException e) {
          return "";
        }
        catch (IOException ex) {
            return "";
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
        }
    }
}
