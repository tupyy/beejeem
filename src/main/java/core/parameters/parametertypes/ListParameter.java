package core.parameters.parametertypes;

import core.parameters.Parameter;
import org.w3c.dom.Element;

import java.util.List;

/**
 * This parameter represents a list of options. 
 */
public class ListParameter<ValueType> extends AbstractParameter<ValueType> {

    private List<ValueType> options;

    public ListParameter(String name, String description, String category, String label, String source) {
        super(name, description, category, label, source);
    }

    public ListParameter(String name, String description, String category, String label, String source,
                         List<ValueType> options,ValueType defaultValue) {
        super(name, description, category, label, source);
        this.options = options;
        setValue(defaultValue);
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {
         setValue(xmlElement.getTextContent());
    }

    @Override
    public void saveValueToXML(Element xmlElement) {

    }

    @Override
    public Parameter<ValueType> clone() {
        ListParameter<ValueType> copy = new ListParameter<ValueType>(getName(),getDescription(),getCategory(),getLabel(),
                getSource(),options,getValue());
        return copy;
    }

    public List<ValueType> getOptions() {
        return options;
    }
}
