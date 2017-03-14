package core.parameters.parametertypes;

import org.w3c.dom.Element;

public class AircraftParameter extends AbstractParameter<Aircraft> {

    public AircraftParameter(Aircraft value) {
        super("aircraft","Aircraft programme","Job","aircraft","internal");
        setValue(value);

    }

    public AircraftParameter(String name,  String description,
                             String category,Aircraft defaultValue,String source) {
        super(name, description, category, "aircraft","internal");
        setValue(defaultValue);
    }

    @Override
    public AircraftParameter clone() {
        AircraftParameter copy = new AircraftParameter(getName(),
                getDescription(), getCategory(), getValue(),getSource());
        copy.setId(this.getID());
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {

    }

    @SuppressWarnings("null")
    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue().toString());
    }

}
