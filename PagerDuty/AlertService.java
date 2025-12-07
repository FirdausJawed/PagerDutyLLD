import java.util.HashMap;
import java.util.Map;

public class AlertService {
    //escalate the alert if needed
    //if the oncall has not ack the alert, then after 10 min again notify him
    private Oncall oncall;
    private Alert alert;
    Map<Integer,Alert> alerts = new HashMap<>();
    Map<Integer, Oncall> escalationMap = new HashMap<>();
    private int idCounter = 0;
    private boolean isDone = false;

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
        idCounter++;
        alerts.put(idCounter, alert);
        System.out.println("alert is created!!");
        return alert;
    }

    //ack alert by oncall
    public void ackAlert(Oncall oncall, Alert alert) {
        System.out.println("alert is acknowdged by primary oncall");
        alert.setStatus(Status.ACK);
    }

    //resolve alert by oncall
    public void resolveAlert(Oncall oncall, Alert alert) {
        System.out.println("issue resolved");
        alert.setStatus(Status.RESOLVED);
    }
}
