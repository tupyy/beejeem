package core.parameters.parametertypes;

import org.w3c.dom.Element;

/**
 * Created by tctupangiu on 12/01/2017.
 */
public class CodeParameter extends StringParameter {

    public CodeParameter(String name) {
        this(name, "");
    }
    /**
     *
     * @param name
     * @param codeTemplate core template
     */
    public CodeParameter(String name,String codeTemplate) {
        super(name, "The template of the python code", "Code", "Code template", "","internal");
        setValue(codeTemplate);
    }

    @Override
    public CodeParameter clone() {
        CodeParameter copy = new CodeParameter(getName(),getValue());
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
