package stes.isami.bjm.components.notifications;

/**
 * Created by tctupangiu on 06/07/2017.
 */
public class NotificationEvent {

    private final String message;
    private final NotiticationType notificationType;
    private final String title;

    public NotificationEvent(NotiticationType notiticationType, String title, String message) {
        this.notificationType = notiticationType;
        this.title = title;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public NotiticationType getNotificationType() {
        return notificationType;
    }

    public String getTitle() {
        return title;
    }

    public enum NotiticationType {
        INFORMATION,
        ERROR
    }


}
