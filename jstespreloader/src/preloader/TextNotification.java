package preloader;

import javafx.application.Preloader;

/**
 * Created by tctupangiu on 28/03/2017.
 */
public class TextNotification implements Preloader.PreloaderNotification {

    private final String notification;

    public TextNotification(String notification) {
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }
}
