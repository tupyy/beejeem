package core.parameters.parametertypes;

import core.parameters.Parameter;
import org.w3c.dom.Element;

/**
 * XML Element parameter class
 */
public class ElementParameter extends AbstractParameter<Element>{

    public ElementParameter(String name,String description,
                           String category) {
        this(name, description, category, null,name,null);
    }

    public ElementParameter(String name,String description,
                            String category,Element defaultValue,String source) {
        super(name, description, category, name,source);
        setValue(defaultValue);
    }


    public ElementParameter(String name,String description,
                           String category,Element defaultValue,String label,String source) {
        super(name, description, category, label,source);
        setValue(defaultValue);
    }

    @Override
    public ElementParameter clone() {
        ElementParameter copy = new ElementParameter(getName(), getDescription(),
                getCategory(), getValue(), getLabel(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {

    }

    @Override
    public void saveValueToXML(Element xmlElement) {

    }

}
