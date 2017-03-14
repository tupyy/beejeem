package core.parameters.parametertypes;

/**
 * Created by cosmin on 18/11/2016.
 */
public enum Aircraft {
    XWB900("A350-900"),
    XWB1000("A350-1000"),
    SANEO("A320NEO"),
    LRNEO("A330NEO"),
    DD("A380"),
    LR("A330/340"),
    SA("A320"),
    WB("A300/310"),
    MT("A400M");

    private final String name;

    Aircraft(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
