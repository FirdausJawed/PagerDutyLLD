public class PhoneCallNotifier implements Notifier {
    public void sendNotification(Oncall oncall) {
        //sending notification via call
        System.out.println("Notification recieved via call to " + oncall.getName());
    }
}