import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertService {
    //escalate the alert if needed
    //if the oncall has not ack the alert, then after 10 min again notify him
    private Oncall oncall;
    private Alert alert;
    Map<Integer,Alert> alerts = new ConcurrentHashMap<>();
    Map<Integer, Oncall> escalationMap = new ConcurrentHashMap<>();
    private AtomicInteger idCounter = new AtomicInteger(0);
    ExecutorService workerPool = Executors.newFixedThreadPool(10); // for multiple alerts at a time
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4); // for scheduler

    // track scheduled futures per alert so we can cancel when resolved/acked
    ConcurrentHashMap<Integer, ScheduledFuture<?>> scheduledReminders = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, ScheduledFuture<?>> scheduledEscalations = new ConcurrentHashMap<>();


    public AlertService(Oncall oncall, Alert alert) {
        this.oncall = oncall;
        this.alert = alert;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Oncall getOncall() {
        return oncall;
    }

    public void setOncall(Oncall oncall) {
        this.oncall = oncall;
    }

    //create alert
    public Alert createAlert() {
        int id = idCounter.incrementAndGet();
        alert.setId(id);
        alerts.put(id, alert);

        // schedule reminder if after 10 min the person did not ack
        ScheduledFuture<?> rem = scheduler.schedule( () -> {
            workerPool.submit(()->alarmOncall(oncall, alert));
        }, 100, TimeUnit.MILLISECONDS);
        
        scheduledReminders.put(id,rem);

        ScheduledFuture<?> esc = scheduler.schedule(() -> {
            workerPool.submit(() -> escalate(alert));
        }, 1500, TimeUnit.MILLISECONDS);

        scheduledEscalations.put(id, esc);

        System.out.println("alert is created!!");
        return alert;
    }

    // called by the scheduled reminder task (runs in worker pool)
    private void alarmOncall(Oncall oncall, Alert alert) {
        Status s = alert.getStatus();
        if (s == Status.NEW) {
            System.out.println("primary oncall alarmed again for alert id=" + alert.getId());
            // here you should call NotificationService (in your PagerDuty flow)
            // leave actual notification invocation to the caller (PagerDuty)
        } else {
            System.out.println("skipping alarmOncall for id=" + alert.getId() + " status=" + s);
        }
    }

    private void escalate(Alert alert) {
        Status s = alert.getStatus();
        if(s == Status.ACK || s == Status.NEW) {
            System.out.println("escalating alert id=" + alert.getId());
            // call NotificationService to notify escalationMap members (PagerDuty will call notificationService)
            // after escalation, mark alert as ESCALATED (optional)
            alert.setStatus(Status.ESCALATED);
        }
        // cleanup after escalation if desired
        int alertId = alert.getId();
        ScheduledFuture<?> rem = scheduledReminders.remove(alertId);
        if (rem != null) rem.cancel(false);
        ScheduledFuture<?> esc = scheduledEscalations.remove(alertId);
        if (esc != null) esc.cancel(false);
    }

    //ack alert by oncall
    public void ackAlert(Oncall oncall, Alert alert) {
        boolean update = alert.compareAndSetStatus(Status.NEW, Status.ACK);
        int alertId = alert.getId();
        if(update) {
            System.out.println("alert is acknowdged by primary oncall");
            alert.ackTime = System.currentTimeMillis();
            //cancel escalation to manager
            ScheduledFuture<?> rem = scheduledReminders.remove(alertId);
            if (rem != null) rem.cancel(false);
            ScheduledFuture<?> esc = scheduledEscalations.remove(alertId);
            if (esc != null) esc.cancel(false);
        }
    }

    //resolve alert by oncall
    public void resolveAlert(Oncall oncall, Alert alert) {
        alerts.remove(alert.getId());
        alert.setStatus(Status.RESOLVED);
        System.out.println("issue resolved");
    }

    public void shutdown() {
        workerPool.shutdown();
        scheduler.shutdown();
    }
}
