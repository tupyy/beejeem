package stes.isami.bjm.components.materialExplorer.business;

/**
 * Event fired by the load library task
 */
public class LoadLibraryEvent {

    private final int progress;
    private final String message;

    public LoadLibraryEvent(String message, int progress) {
        this.message = message;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }
}
