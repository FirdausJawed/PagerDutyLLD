import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    List<Notifier> notificationVia = new ArrayList<>();
    public NotificationService(List<Notifier> notificationVia) {
        this.notificationVia = notificationVia;
    }

    public List<Notifier> getNotificationVia() {
        return notificationVia;
    }

    public void setNotificationVia(List<Notifier> notificationVia) {
        this.notificationVia = notificationVia;
    }

    public void sendNotifications(Oncall oncall) {
        for(Notifier n : notificationVia) {
            n.sendNotification(oncall);
        }
    }
}
