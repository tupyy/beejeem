package core.parameters.parametertypes;

import org.w3c.dom.Element;

import java.text.NumberFormat;

/**
 * Created by cosmin on 28/11/2016.
 */
public class IntegerParameter extends AbstractParameter<Integer> {

     public IntegerParameter(String name, String description,
                            String category,String source) {
        this(name, description, category, null,name);
    }

    public IntegerParameter(String name,String description,
                           String category,Integer defaultValue,String source) {
        this(name, description, category, defaultValue,name,source);
    }

    public IntegerParameter(String name,String description,
                           String category, Integer defaultValue,String label,String source) {
        this(name, description, category,null,defaultValue,label,source);
    }

    public IntegerParameter(String name,  String description,
                           String category,NumberFormat numberFormat,
                            Integer defaultValue,String codename,String source) {
        super(name, description, category, codename,source);
        setValue(defaultValue);
    }

    @Override
    public IntegerParameter clone() {
        IntegerParameter copy = new IntegerParameter(getName(), getDescription(),
                getCategory(), getValue(), getLabel(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {
//        final String textValue = xmlElement.getTextContent();
//        if (Strings.isNullOrEmpty(textValue))
//            return;
//        final Double content = Double.parseDouble(textValue);
//        setValue(content);
    }

    @SuppressWarnings("null")
    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue().toString());
    }
}
