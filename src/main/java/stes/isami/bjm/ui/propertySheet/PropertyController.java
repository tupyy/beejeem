package stes.isami.bjm.ui.propertySheet;

import com.google.common.eventbus.EventBus;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stes.isami.core.job.Job;
import stes.isami.core.parameters.ParameterSet;
import stes.isami.bjm.ui.MainController;

/**
 * Created by tctupangiu on 17/03/2017.
 */
public class PropertyController  {

    private PropertySheet propertySheet;
    private PropertyModel model;

    private static final Logger logger = LoggerFactory
            .getLogger(MainController.class);

    private EventBus propertyControllerEventBus = new EventBus();

    public PropertyController(PropertyModel model) {
        this.model = model;
        initializeController(model);
        propertySheet.setMode(PropertySheet.Mode.CATEGORY);
    }

    public PropertyController() {
        this.model = new PropertyModel();
        initializeController(model);
        propertySheet.setMode(PropertySheet.Mode.CATEGORY);
    }

    /**
     * Register listener
     * @param propertyListener
     */
    public void registerListener(PropertyListener propertyListener) {
        propertyControllerEventBus.register(propertyListener);
    }

    public PropertySheet getPropertySheet() {
        return propertySheet;
    }


    /**
     * Return parameter set
     * @return
     */
    public ParameterSet getData() {
        return model.getData();
    }

    public void loadJob(Job j) {
        propertySheet.setDisable(!j.isEditable());
        model.setData(j.getParameters(),new ParameterChangeListener());
    }

    public void updateJob(Job job) {

        if (job.isEditable()) {
            model.updateData(job.getParameters());
        }

        propertySheet.setDisable(!job.isEditable());
    }

    public void clear() {
        model.clear();
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
            } else if (simpleItem.getValue() instanceof Integer || simpleItem.getValue() instanceof Double) {
                return Editors.createNumericEditor(simpleItem);
            } else {
                return Editors.createTextEditor(simpleItem);
            }
        });

    }

    private class ParameterChangeListener implements ChangeListener{

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            SimpleObjectProperty simpleObjectProperty = (SimpleObjectProperty) observable;
//            logger.info("Parameter {} value updated: {}",simpleObjectProperty.getName(), simpleObjectProperty.getValue());
            propertyControllerEventBus.post(new PropertyEvent(simpleObjectProperty,newValue));
        }
    }
}
