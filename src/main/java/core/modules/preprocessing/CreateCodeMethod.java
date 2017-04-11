package core.modules.preprocessing;

import core.modules.Method;
import core.modules.MethodResult;
import core.modules.StandardMethodResult;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.BooleanParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This methods creates the python code and write it into the python file.
 */
public class CreateCodeMethod implements Method {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private static final String METHOD_NAME = "CreateCodeMethod";
    private final String moduleName;
    private String filePath;
    private final ParameterSet parameterSet;
    private final UUID jobID;

    /**
     * @param jobID ID of the job associated with the python code
     * @param parameterSet the parameters with which the final python code will be created
     */
    public CreateCodeMethod(String moduleName,UUID jobID,ParameterSet parameterSet) {
        this.moduleName = moduleName;
        this.jobID = jobID;
        this.parameterSet = parameterSet;
    }

    @Override
    public String getName() {
        return METHOD_NAME;
    }

    @Override
    public MethodResult execute() {
        StandardMethodResult result = new StandardMethodResult(moduleName,METHOD_NAME,jobID,MethodResult.OK);
        String code = (String) parameterSet.getParameter("pythonCode").getValue();
        filePath = createPythonFilePath((String)parameterSet.getParameter("filename").getValue(),
                (String) parameterSet.getParameter("temporaryFolder").getValue());

        ArrayList<String> parameters = (ArrayList<String>) getParametersName(code);
        for (String name: parameters) {
            if (name.equals("basename")) {
                code =  code.replace("@".concat(name).concat("@"),getBasename((String)parameterSet.getParameter("filename").getValue()));
            }
            else if (name.equals("mission")) {
                String mission = getMissionName((String)parameterSet.getParameter("filename").getValue());
                if (mission.isEmpty()) {
                    return new StandardMethodResult(moduleName,METHOD_NAME, jobID, MethodResult.ERROR, "Cannot find mission definition in the filename");
                }
                code = code.replace("@".concat(name).concat("@"), mission);
            }
            else if (name.equals("filename")) {
                code =  code.replace("@".concat(name).concat("@"),getFileName((String)parameterSet.getParameter("filename").getValue()));
            }
            else{
                try {
                    Parameter param = parameterSet.getParameter(name);

                    if (param instanceof BooleanParameter) {
                        Boolean value = (Boolean) ((BooleanParameter) param).getValue();
                        if (value) {
                            code = code.replace("@".concat(name).concat("@"), "Yes");
                        }
                        else {
                            code = code.replace("@".concat(name).concat("@"), "No");
                        }
                    }
                    else {
                        code = code.replace("@".concat(name).concat("@"), param.getValue().toString());

                    }
                } catch (IllegalArgumentException ex) {
                    logger.info(ex.getMessage());
                    result.addErrorMessage(ex.getMessage());
                    result.setExitCode(MethodResult.ERROR);
                }
            }
        }

        //create the file
        try {
            FileWriter fileOut = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(fileOut);
            out.print(code);
            out.close();
        }
        catch (IOException ex) {
            return new StandardMethodResult(moduleName,METHOD_NAME,jobID,MethodResult.ERROR,ex.getMessage());
        }

        return result;
    }

    @Override
    public void cancel() {

    }

    /**
     * Create a list with all the parameters found in the code. A parameter starts with the @ symbol
     * @param code
     * @return
     */
    private List<String> getParametersName(String code) {
        List<String> names = new ArrayList<>();
        Pattern searchPattern = Pattern.compile("@\\w+@");
        Matcher m = searchPattern.matcher(code);

        while(m.find()) {
            //write what we found without @ character
            names.add(code.substring(m.start()+1,m.end()-1));
        }

        return names;
    }

    /**
     * Get the basename
     * @param filename absolute path of the file
     * @return
     */
    private String getBasename(String filename){
        return filename.substring(filename.lastIndexOf("\\")+1,filename.lastIndexOf("."));
    }

    private String getFileName(String filename) {
        String filename2 = filename.substring(filename.lastIndexOf("\\")+1);
        return filename2;
    }

    private String getMissionName(String filename) {

        if (filename.contains("SR")) {
            return "SR";
        }
        else if (filename.contains("MR")) {
            return "MR";
        }
        else if (filename.contains("LR")) {
            return "LR";
        }

        return "";
    }

    private String createPythonFilePath(String filename,String tempFolder) {
       return tempFolder.concat(File.separator).concat(getBasename(filename).concat(".py"));
    }

}
