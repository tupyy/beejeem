package stes.isami.bjm.components;

import com.google.common.eventbus.Subscribe;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import stes.isami.bjm.eventbus.CoreEvent;
import stes.isami.bjm.eventbus.ComponentEvent;

import java.io.IOException;

/**
 * Interface to be implemented by every component of the main window
 */
public interface Component {

    /**
     * Return the root pane of the component
     * @return root pane
     */
    public Pane getRootPane() throws IOException;

}
