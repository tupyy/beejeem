package stes.isami.bjm.ui;

import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Interface to be implemented by every component of the main window
 */
public interface UIComponent {

    /**
     * Return the root pane of the component
     * @return root pane
     */
    public Pane getRootPane() throws IOException;

}
