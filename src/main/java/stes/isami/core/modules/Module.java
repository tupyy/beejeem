package stes.isami.core.modules;

/**
 * This interface represents any module.
 * A module is a component which creates a list of tasks based on a defined method.
 */
public interface Module {

    /**
     * Get the name of the module
     * @return module name
     */
    public String getName();

}
