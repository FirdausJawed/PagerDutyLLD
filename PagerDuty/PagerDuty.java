import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PagerDuty {
    public AlertService alertService;
    public NotificationService notificationService;

    public PagerDuty(AlertService alertService, NotificationService notificationService) {
        this.alertService = alertService;
        this.notificationService = notificationService;
    }

    public void initializeNotifiers(List<Notifier> notifyVia) {
        Notifier e = new EmailNotifier();
        Notifier s = new SMSNotifier();
        Notifier p = new PhoneCallNotifier();

        notifyVia.add(e);
        notifyVia.add(s);
        notifyVia.add(p);
    }

    //alert creation
    public Alert alertCreation() {
        Alert alert = alertService.createAlert();
        return alert;
    }

    //notify to oncall
    public void notifyOncall(Oncall oncall, Alert alert) {
        List<Notifier> notifyVia = new ArrayList<>();
        initializeNotifiers(notifyVia);
        notificationService.setNotificationVia(notifyVia);
        notificationService.sendNotifications(oncall);
    }

    //if after 15 min, the alert is not resolved, escalate it
    public void escalate(Map<Integer, Oncall> escalationMap) {
        Alert alert = alertService.createAlert();

        long ackTime = alert.ackTime;
        long systemTime = System.currentTimeMillis();

        long alertAge = systemTime - ackTime;
        long thresholdAge = TimeUnit.MINUTES.toMillis(15);

        while(alertAge < thresholdAge) {}

        for(Oncall oncall : escalationMap.values()) {
            notificationService.sendNotifications(oncall);
        }
        System.out.println("escalated the issue to secondary oncall and the manager");
    }

    //if oncall has not ack the alert, call him again after 10 min
    public void alarmOncall(Oncall oncall, Alert alert) {
        if((alert.getStatus() == Status.NEW)
            && (alert.creationTime-System.currentTimeMillis()) >= TimeUnit.MINUTES.toMillis(10)) {
            notifyOncall(oncall, alert);
        }
        System.out.println("primary oncall alarmed again");
    }
}
