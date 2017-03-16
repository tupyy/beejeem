package core.parameters.parametertypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class StringParameter extends AbstractParameter<String> {

    public StringParameter(String name,String description,
            String category) {
        this(name, description, category, null,name, "");
    }

    public StringParameter(String name,String description,
                           String category,String defaultValue) {
        this(name, description, category, defaultValue,name, "");
    }

    public StringParameter(String name,String description,
            String category,String defaultValue,String label,String source) {
        super(name, description, category, label,source);
        setValue(defaultValue);
    }

    @Override
    public StringParameter clone() {
        StringParameter copy = new StringParameter(getName(), getDescription(),
                getCategory(), getValue(), getLabel(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {
        setValue(xmlElement.getTextContent());
    }

    public void setValueFromString(String s) {
        setValue(s);
    }

    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue());
    }
}
