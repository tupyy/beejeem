package stes.isami.core.parameters.parametertypes;

import java.util.UUID;

import stes.isami.core.parameters.Parameter;

/**
 * This parameter stores filenames
 */
public abstract class AbstractParameter<ValueType>
        implements Parameter<ValueType> {

    private UUID id = java.util.UUID.randomUUID();

    private ValueType value =  null;

    private final String name;
    private String description;
    private String category;
    private String label;
    private Boolean validParameter;
    private String source;

    public AbstractParameter(String name, String description,
                             String category, String label,String source) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.label = label;
        this.source = source;
        this.validParameter = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public ValueType getValue() {
        return value;
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getLabel() { return label;  }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void setValue(Object newValue) {
        value =(ValueType) newValue;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    abstract public Parameter<ValueType> clone();

    /**
     * Return the validity of the parameter.
     * A parameter is valid if the name is defined and the value is of the right type
     * @return
     */
    public boolean isValid() {
        if (value != null) {
            return true;
        }

        return false;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

