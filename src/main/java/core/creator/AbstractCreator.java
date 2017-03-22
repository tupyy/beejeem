package core.creator;

import core.job.Job;
import core.job.JobState;
import core.job.ModuleController;
import core.modules.Module;
import core.modules.ModuleStarter;
import core.parameters.Parameter;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.*;
import core.util.XMLWorker;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.JStesCore.getCoreEngine;

/**
 * Created by tctupangiu on 21/03/2017.
 */
public class AbstractCreator implements Creator {

    @Override
    public Job createJob(File inputFile, Map<String, String> parameterValues, ParameterSet parameterSet, CreatorLog creatorLog) throws IllegalArgumentException, IOException {
        return null;
    }

    @Override
    public List<Job> createJobs(List<File> inputFiles, ParameterSet parameterSet, List<Element> moduleElements, CreatorLog creatorLog) throws IOException {
        return null;
    }

    /**
     * Create the module parameters
     * @param modulesElement
     */
    public List<ModuleController> createModuleParameter(List<Element> modulesElement, CreatorLog log) {
        List<ModuleController> moduleSet = new ArrayList<>();
        XMLWorker xmlWorker = new XMLWorker();

         for (Element elem: modulesElement) {
            if (elem.getNodeName().equals("module")) {
                try {
                    ModuleController moduleController;
                    if (xmlWorker.getElementByName(elem,"trigger") != null) {
                        moduleController = new ModuleController(xmlWorker.getElementByName(elem,"name").getTextContent(),getStatus(xmlWorker.getElementByName(elem,"trigger").getTextContent()));
                    }
                    else {
                        moduleController = new ModuleController(xmlWorker.getElementByName(elem,"name").getTextContent(), JobState.NONE);
                    }

                    Class<? extends Module> className = (Class<? extends Module>) Class.forName(xmlWorker.getElementByName(elem,"name").getTextContent());
                    Module module = getCoreEngine().getModuleStarter().getModuleInstance(className);
                    for(String methodName: module.getMethodsName()) {
                        moduleController.addMethod(methodName);
                    }
                    moduleSet.add(moduleController);
                } catch (ClassNotFoundException e) {
                    log.error(String.format("Class not found for module %s",xmlWorker.getElementByName(elem,"name").getTextContent()));
                }
            }

        }

        return moduleSet;
    }

    /**
     * Map the name of the status to its value in the Jobstate
     * @param triggerName
     * @return
     */
    private int getStatus(String triggerName) {

        return JobState.getStatusCode(triggerName);

    }

    /**
     * Update the whole set
     * @param parameterSet
     * @param valuesMap
     */
    public void updateParameterSet(ParameterSet parameterSet, HashMap<String,String> valuesMap, CreatorLog creatorLog) {

        for(Parameter p: parameterSet) {
            if (valuesMap.containsKey(p.getName())) {
                String value = valuesMap.get(p.getName());
                updateParameter(p, value,creatorLog);
                creatorLog.info(String.format("Parameter %s updated with value %s",p.getName(),value));
            }
        }
    }

    /**
     * Update parameter from String value.
     * The method is casting the value to the ValueType of the parameter.
     * @param p
     * @param value
     */
    private void updateParameter(Parameter p, String value,CreatorLog log) {
        if (p instanceof BooleanParameter) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                p.setValue(Boolean.parseBoolean(value));
            }
            else {
                log.error("Cannot cast string to boolean for parameter ".concat(p.getName()));
            }
        }
        else if (p instanceof IntegerParameter) {
            try {
                int valueInt = Integer.parseInt(value);
                p.setValue(valueInt);
            }
            catch (NumberFormatException e) {
                log.error("NumberFormatException for parameter ".concat(p.getName()));
            }
        }
        else if (p instanceof DoubleParameter) {
            try {
                double valueDouble = Double.parseDouble(value);
                p.setValue(valueDouble);
            }
            catch (NumberFormatException e) {
                log.error("NumberFormatException for parameter ".concat(p.getName()));
            }
        }
        else if (p instanceof StringParameter) {
            p.setValue(value);
        }
        else if (p instanceof ListParameter) {
            p.setValue(value);
        }
    }
}
