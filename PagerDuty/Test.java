import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String args[]) {
        // initialization of everything
        Oncall primaryOncall = new Oncall("Alice", "alice@pagerduty.com", "555-0100");
        primaryOncall.setId(1);

        Alert alert = new Alert("Database Connection Timeout", "Database is not responding");
        alert.setId(101);

        AlertService alertService = new AlertService(primaryOncall, alert);
        List<Notifier> notifiers = new ArrayList<>();
        notifiers.add(new EmailNotifier());
        notifiers.add(new SMSNotifier());
        notifiers.add(new PhoneCallNotifier());
        NotificationService notificationService = new NotificationService(notifiers);

        PagerDuty pagerDuty = new PagerDuty(alertService, notificationService);

        Map<Integer, Oncall> escalationMap = new HashMap<>();
        Oncall secondary = new Oncall("Bob", "bob@example.com", "+1-555-0002");
        Oncall manager = new Oncall("Alice", "alice@example.com", "+1-555-0001");
        escalationMap.put(0, primaryOncall);
        escalationMap.put(1, secondary);
        escalationMap.put(2, manager);

        // pager created the alert
        Alert createdAlert = pagerDuty.alertCreation();

        //notify oncall by pager
        pagerDuty.notifyOncall(primaryOncall, createdAlert);

        // if the alert not ack by oncall within 10 min, alarm again
        pagerDuty.alarmOncall(primaryOncall, createdAlert);

        // Acknowledge alert by oncall
        alertService.ackAlert(primaryOncall, createdAlert);

        // if issue not resolved in 15 min, escalate it
        pagerDuty.escalate(escalationMap);
    }
}
