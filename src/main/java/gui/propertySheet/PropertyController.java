package gui.propertySheet;

import core.job.Job;
import core.job.JobExecutionProgress;
import core.parameters.ParameterSet;
import gui.mainview.sidepanel.ComponentController;
import javafx.scene.control.Control;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

/**
 * Created by tctupangiu on 17/03/2017.
 */
public class PropertyController implements ComponentController{

    private PropertySheet propertySheet;
    private PropertyModel model;

    public PropertyController(PropertyModel model) {
        this.model = model;
        initializeController(model);
    }

    public PropertyController() {
        this.model = new PropertyModel();
        initializeController(model);
    }

    public void clearPropertySheet() {
        model.getPropertySheetItems().clear();
    }

    public PropertySheet getPropertySheet() {
        return propertySheet;
    }


    public void setEditable(boolean editable) {
        this.propertySheet.setDisable((editable==true) ? false: true);
    }

    @Override
    public void loadJob(Job j) {
        model.setParameterSet(j.getParameters());
    }

    @Override
    public void updateJob(Job job) {
        model.updateParameterSet(job.getParameters());
    }

    @Override
    public void setJobProgressLogger(JobExecutionProgress jobProgressLogger) {

    }


    private void initializeController(PropertyModel model) {
        propertySheet = new PropertySheet(model.getPropertySheetItems());
        propertySheet.setMaxWidth(Control.USE_COMPUTED_SIZE);
        propertySheet.setPrefWidth(Control.USE_COMPUTED_SIZE);

        getPropertySheet().setPropertyEditorFactory(param -> {
            PropertyModel.SimpleItem simpleItem = (PropertyModel.SimpleItem) param;

            if(simpleItem.getType().toString().contains("java.util.List")) {
                return Editors.createChoiceEditor(simpleItem,simpleItem.getOptions());
            } else if (simpleItem.getValue() instanceof Boolean) {
                return Editors.createCheckEditor(simpleItem);
            } else if (simpleItem.getValue() instanceof Integer) {
                return Editors.createNumericEditor(simpleItem);
            } else {
                return Editors.createTextEditor(simpleItem);
            }
        });
    }
}
