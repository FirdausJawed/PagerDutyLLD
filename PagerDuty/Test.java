// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class Test {
//     public static void main(String args[]) {
//         // initialization of everything
//         Oncall primaryOncall = new Oncall("Alice", "alice@pagerduty.com", "555-0100");
//         primaryOncall.setId(1);

//         Alert alert = new Alert("Database Connection Timeout", "Database is not responding");
//         alert.setId(101);

//         AlertService alertService = new AlertService(primaryOncall, alert);
//         List<Notifier> notifiers = new ArrayList<>();
//         notifiers.add(new EmailNotifier());
//         notifiers.add(new SMSNotifier());
//         notifiers.add(new PhoneCallNotifier());
//         NotificationService notificationService = new NotificationService(notifiers);

//         PagerDuty pagerDuty = new PagerDuty(alertService, notificationService);

//         Map<Integer, Oncall> escalationMap = new HashMap<>();
//         Oncall secondary = new Oncall("Bob", "bob@example.com", "+1-555-0002");
//         Oncall manager = new Oncall("Alice", "alice@example.com", "+1-555-0001");
//         escalationMap.put(0, primaryOncall);
//         escalationMap.put(1, secondary);
//         escalationMap.put(2, manager);

//         // pager created the alert
//         Alert createdAlert = pagerDuty.alertCreation();

//         //notify oncall by pager
//         pagerDuty.notifyOncall(primaryOncall, createdAlert);

//         // if the alert not ack by oncall within 10 min, alarm again
//         pagerDuty.alarmOncall(primaryOncall, createdAlert);

//         // Acknowledge alert by oncall
//         alertService.ackAlert(primaryOncall, createdAlert);

//         // if issue not resolved in 15 min, escalate it
//         pagerDuty.escalate(escalationMap);
//     }
// }


import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Oncall primary = new Oncall("Alice", "alice@example.com", "9999999999");
        Alert firstAlert = new Alert("DiskFull", "Disk usage > 95%");
        AlertService alertService = new AlertService(primary, firstAlert);

        // prepare notification service (empty list for now)
        List<Notifier> notifiers = new ArrayList<>();
        NotificationService notificationService = new NotificationService(notifiers);
        PagerDuty pd = new PagerDuty(alertService, notificationService);

        // Simulate concurrent incoming alerts
        IntStream.range(0, 5).forEach(i -> {
            new Thread(() -> {
                Alert a = new Alert("Alert-" + Thread.currentThread().getId(), "desc");
                // create separate AlertService instance won't be necessary in prod; we reuse alertService for demo:
                alertService.setAlert(a);
                Alert created = alertService.createAlert();
                // immediately notify primary
                pd.notifyOncall(primary, created);
            }).start();
        });

        // wait for threads to finish creation
        Thread.sleep(2000);

        // simulate ack on first alert
        Alert created = alertService.getAlert(); // last set alert (demo purpose)
        if (created != null) {
            alertService.ackAlert(primary, created);
        }

        // let scheduled tasks (if any) run in background for demo; in real unit tests you'd use shorter durations
        Thread.sleep(1000);

        alertService.shutdown();
        notificationService.shutDown();
    }
}
