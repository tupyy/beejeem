
package core.parameters.parametertypes;
import core.parameters.Parameter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        NodeList nodeList = xmlElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equalsIgnoreCase("description")) {
                setDescription(node.getTextContent());
            }
            if (node.getNodeName().equalsIgnoreCase("label")) {
                setLabel(node.getTextContent());
            }
            if (node.getNodeName().equalsIgnoreCase("category")) {
                setCategory(node.getTextContent());
            }
            if (node.getNodeName().equalsIgnoreCase("value")) {
                if (node.getTextContent().equalsIgnoreCase("true") || node.getTextContent().equalsIgnoreCase("false")) {
                    super.setValue(Boolean.parseBoolean(node.getTextContent()));
                }
            }
        }
    }

    @Override
    public void saveValueToXML(Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(String.valueOf(getValue()));
    }

}
