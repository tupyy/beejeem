package stes.isami.core.parameters.parametertypes;

import java.text.NumberFormat;
import org.w3c.dom.Element;


public class DoubleParameter extends AbstractParameter<Double> {

    private NumberFormat numberFormat = null;

    public DoubleParameter(String name,String description,
            String category,String source) {
        super(name, description, category, name,source);
    }

    public DoubleParameter(String name,String description,
                           String category,Double defaultValue,String source) {
        this(name, description, category, defaultValue,name,source);
    }

    public DoubleParameter(String name,String description,
            String category, Double defaultValue,String label,String source) {
        this(name, description, category,null,defaultValue,label, source);
    }

    public DoubleParameter(String name,  String description,
            String category,NumberFormat numberFormat,
            Double defaultValue,String codename,String source) {
        super(name, description, category, codename,source);
        setValue(defaultValue);
        this.numberFormat = numberFormat;
    }

    @Override
    public DoubleParameter clone() {
        DoubleParameter copy = new DoubleParameter(getName(), getDescription(),
                getCategory(), getNumberFormat(), getValue(), getLabel(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {

        try {
            setValue(Double.parseDouble(xmlElement.getTextContent()));
        }
        catch (NumberFormatException ex) {
        }


    }

    @SuppressWarnings("null")
    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue().toString());
    }

}
