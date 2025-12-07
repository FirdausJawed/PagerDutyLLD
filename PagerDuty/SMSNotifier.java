public class SMSNotifier implements Notifier{
    public void sendNotification(Oncall oncall) {
        //sending notification via sms
        System.out.println("Notification recieved via SMS to " + oncall.getName());
    }
}
