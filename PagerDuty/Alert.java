public class Alert {
    int id;
    String name;
    String description;
    Status status;
    long creationTime;
    long ackTime;

    public Alert(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
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
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getAckTime() {
        return ackTime;
    }

    public void setAckTime(long ackTime) {
        this.ackTime = ackTime;
    }
}
