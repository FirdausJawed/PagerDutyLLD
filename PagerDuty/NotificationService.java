import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService {
    List<Notifier> notificationVia = new ArrayList<>();
    private final ExecutorService notifierPool = Executors.newCachedThreadPool();

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
            notifierPool.submit(()->n.sendNotification(oncall));
        }
    }

    public void shutDown() {
        notifierPool.shutdown();
    }
}
