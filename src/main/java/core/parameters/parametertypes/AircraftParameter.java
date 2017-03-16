package core.parameters.parametertypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        NodeList nodeList = xmlElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equalsIgnoreCase("value")) {
                setValue(Aircraft.valueOf(node.getTextContent()));
            }
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
