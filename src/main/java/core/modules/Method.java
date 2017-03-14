package core.modules;

/**
 * This interface represents a methods. A methods represent an action to be run inside a Callable.
 */
public interface Method {

    /**
     * Return the name of the methods
     * @return
     */
    public String getName();
    /**
     * Performs the action
     * @return  a MethodResult object
     */
    public MethodResult execute();

    /**
    * Cancel a running algorithm. This methods can be called from any thread.
    */
    void cancel();
}
