import java.util.concurrent.atomic.AtomicReference;

public class Alert {
    int id;
    String name;
    String description;
    private final AtomicReference<Status> status;
    long creationTime;
    long ackTime;

    public Alert(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = new AtomicReference<>(Status.NEW);
        this.creationTime = System.currentTimeMillis();
        this.ackTime = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status newStatus) {
        status.set(newStatus);
    }

    public long getAckTime() {
        return ackTime;
    }

    public void setAckTime(long ackTime) {
        this.ackTime = ackTime;
    }

    /**
     * Compare-and-set style status update. Returns true if update happened.
     */
    public boolean compareAndSetStatus(Status expected, Status update) {
        return status.compareAndSet(expected, update);
    }
}
