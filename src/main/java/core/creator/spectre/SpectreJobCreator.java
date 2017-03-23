package core.creator.spectre;

import core.creator.AbstractCreator;
import core.creator.CreatorLog;
import core.job.Job;
import core.job.SimpleJob;
import core.parameters.ParameterSet;
import core.util.XMLWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.*;
import java.util.*;

/**
 * This method creates the parameter set for a spectre job.
 * For each job created a parameter set will be returned.
 * <br>For the job entries which generated an error, a StringParameter will be returned with the description of the error.
 * The name of the StringParameter will be set to the name of the stf file.
 */
public class SpectreJobCreator extends AbstractCreator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Number of columns of the SG_data_file.txt file
     */
    private final int COLUMNS_COUNT = 15;
    private final String spectreParamNames="name,materialName,materialReference,materialOrientation,fatigueConfiguration,propaConfiguration," +
            "ksnul,compression,traceFile,sigmaFile,nameClass,spectrumType";
    private final List<Integer> columnIndex = Collections.unmodifiableList(Arrays.asList(0,1,2,3,4,5,7,8,9,10,12,14));

    XMLWorker xmlWorker = new XMLWorker();

    public SpectreJobCreator() {

    }

    @Override
    public Job createJob(File inputFile, Map<String, String> parameterValues, ParameterSet parameterSet, CreatorLog creatorLog) throws IllegalArgumentException, IOException {
        return null;
    }

    @Override
    public List<Job> createJobs(List<File> inputFiles, ParameterSet parameterSet, List<Element> moduleElements, CreatorLog creatorLog) throws IllegalArgumentException, IOException {
        List<Job> jobs = new ArrayList<>();

        //create the array of value maps
            ArrayList<HashMap<String,String>> paramValues = new ArrayList<>();

            for (File f: inputFiles) {
                if (f.getName().endsWith("txt")) {
                    if (checkSpectrumGeneratorFile(f,creatorLog) > 0 ) {
                        creatorLog.error(String.format("File %s is not a input file for spectrum jobs",f.getName()));
                    }

                    try {
                        getSpectreData(f, paramValues);
                    } catch (IOException e) {
                        creatorLog.fatalError(String.format("Error reading from file %s. Error: %s",f.getName(),e.getMessage()));
                        throw new IOException(e);
                    }
                }
            }

            if ( paramValues.size() == 0) {
                creatorLog.fatalError("Cannot extract spectre values from the spectrum text file");

            }
            //create a map with filenames
            HashMap<String,String> filesMap = createFileMap(inputFiles,creatorLog);

            for(Map.Entry fileEntry: filesMap.entrySet()) {
               try {
                   for (HashMap<String, String> paramMap : paramValues) {
                       if (paramMap.get("name").equals(fileEntry.getKey())) {
                           ParameterSet newParameters = parameterSet.clone();
                           newParameters.getParameter("filename").setValue(fileEntry.getValue());
                           updateParameterSet(newParameters, paramMap,creatorLog);

                           SimpleJob job = new SimpleJob(newParameters,createModuleParameter(moduleElements,creatorLog));
                           jobs.add(job);
                           break;
                       }
                   }
               }
                catch (IllegalArgumentException e) {
                   creatorLog.fatalError("Parameter filename not found in the input parameter set. Abort");
                   throw new IllegalArgumentException(e);
                }
            }

        return jobs;

    }



    /**
     * Read the dat file and create an entry for each line. For each entry a hashmap is returned with the key as
     * the name of the parameter and the value as the value of the parameter
     * @param spectrumTextFile
     * @return
     * @throws IOException
     */
    private void getSpectreData(File spectrumTextFile,ArrayList<HashMap<String,String>> values) throws IOException {

        String line;
        String[] spectreParameterNames = spectreParamNames.split(",");

        BufferedReader br = new BufferedReader(new FileReader(spectrumTextFile));
        while (( line = br.readLine()) != null ) {
            if ( ! line.startsWith("Point_name")) {
                HashMap<String, String> entry = new HashMap<>();
                String[] buffer = line.split("\\s+");

                try {
                    int j = 0;
                    for (Integer i: columnIndex) {
                        entry.put(spectreParameterNames[j], buffer[i]);
                        j++;
                    }
                values.add(entry);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            }
        }

    }



    /**
     * Creates a map of filenames having as key the name of file without extention
     * @param stfFiles
     * @return
     */
    private HashMap<String,String> createFileMap(List<File> stfFiles,CreatorLog creatorLog) {
        HashMap<String,String> map = new HashMap<>();

        for (File f: stfFiles) {
            if (f.getName().endsWith("stf")) {
                String filename = f.getName();
                String key = filename.substring(0, filename.lastIndexOf("."));
                map.put(key, f.getAbsolutePath());
                creatorLog.info(String.format("Entry added: %s %s",key,f.getAbsolutePath()));
            }
        }

        return map;
    }

    /**
     * Verify that the SG_data_file.txt has the right format. We expect 13 columns without the first line
     * @param f
     * @param creatorLog
     * @return the number of lines which have the right format
     */
    private int checkSpectrumGeneratorFile(File f,CreatorLog creatorLog) {
        int countLines = 0;

        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(f));
            while (( line = br.readLine()) != null ) {
                HashMap<String,String> entry = new HashMap<>();
                String[] buffer = line.split("\\s+");

                if (! buffer[0].equals("Point_name")) {
                    if (buffer.length != COLUMNS_COUNT) {
                        creatorLog.error(String.format("File %s: Line %s is not in the right format",f.getName(),line));
                    }
                    else {
                        countLines++;
                    }
                }

            }
        } catch (FileNotFoundException e) {
            creatorLog.error("File ".concat(f.getAbsolutePath()).concat(" not found."));
        } catch( IOException ex) {
            creatorLog.error("Cannot read from ".concat(f.getAbsolutePath()));
        }

        return countLines;
    }

}
