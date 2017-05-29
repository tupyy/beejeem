
package stes.isami.core.parameters.parametertypes;
import stes.isami.core.parameters.Parameter;
import org.w3c.dom.Element;

public class BooleanParameter extends AbstractParameter<Boolean> {

    public BooleanParameter(String name, String description,
            String category,String source) {
        this(name, description, category,null,name,source);
    }

    public BooleanParameter(String name, String description,
                            String category, Boolean defaultValue) {
        this(name, description, category,defaultValue,name, "");
    }

    public BooleanParameter(String name, String description,
            String category, Boolean defaultValue,String label,String source) {
        super(name, description, category, label,source);
        setValue(defaultValue);
    }

    @Override
    public Parameter<Boolean> clone() {
        BooleanParameter copy = new BooleanParameter(getName(),
                getDescription(), getCategory(), getValue(), getLabel(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {
        if (xmlElement.getTextContent().equalsIgnoreCase("true") || xmlElement.getTextContent().equalsIgnoreCase("false")) {
            super.setValue(Boolean.parseBoolean(xmlElement.getTextContent()));
        }

    }

    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(String.valueOf(getValue()));
    }

}
