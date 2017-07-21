package stes.isami.bjm.ui.notifications;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;


/**
 * Shows notifications when job are finished
 */
public class NotificationCenter{

    public NotificationCenter() {

    }


    @Subscribe
    public void onNotificationEvent(NotificationEvent notificationEvent) {
        switch (notificationEvent.getNotificationType()) {
            case ERROR:
                showErrorNotification(notificationEvent.getTitle(),notificationEvent.getMessage());
                break;
            case INFORMATION:
                showInformationNotification(notificationEvent.getTitle(),notificationEvent.getMessage());
                break;
        }
    }

    private void showErrorNotification(String title,String text) {

        Platform.runLater(() -> {
            Notifications.create()
                    .text(text)
                    .title(title)
                    .showError();
        });

    }

    private void showInformationNotification(String title,String text) {

        Platform.runLater(() -> {
            Notifications.create()
                    .text(text)
                    .title(title)
                    .showInformation();
        });
    }

}
