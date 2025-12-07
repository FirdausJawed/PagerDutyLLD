public class EmailNotifier implements Notifier {
    public void sendNotification(Oncall oncall) {
        //sending notification via email
        System.out.println("Notification recieved via email to "+ oncall.getName() );
    }
}
