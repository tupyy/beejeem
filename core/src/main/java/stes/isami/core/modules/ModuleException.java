package stes.isami.core.modules;

/**
 * Exception thrown by a module
 */
public class ModuleException extends Exception {

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message,Throwable throwable) {
        super(message,throwable);
    }
}
