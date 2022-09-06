package io.shape.jobs;

import io.shape.Polygon;
import io.shape.model.Prospect;
import io.shape.model.ProspectActivity;
import io.shape.repo.ProspectRepo;
import io.shape.service.SmsService;
import org.quartz.*;

import java.util.List;

public class NotificationJob implements Job {

    JobKey jobKey;
    ProspectRepo prospectRepo;
    SmsService smsService;

    public NotificationJob(){
        this.jobKey = new JobKey(Polygon.NOTIFICATION_JOB, Polygon.NOTIFICATION_GROUP);
    }

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        System.out.println("checking for activities...");

        try{

            JobDetail jobDetail = jobContext.getScheduler().getJobDetail(this.jobKey);
            this.prospectRepo = (ProspectRepo) jobDetail.getJobDataMap().get(Polygon.PROSPECT_REPO_KEY);
            this.smsService = (SmsService) jobDetail.getJobDataMap().get(Polygon.SMS_SERVICE_KEY);

            List<ProspectActivity> prospectActivities = prospectRepo.getIncompleteActivities();

            for(ProspectActivity activity: prospectActivities){
                Long now = Polygon.getDateTimezone(activity.getTimeZone());
                Long fiveMins = Polygon.getDateTimezoneMins(5, activity.getTimeZone());

                if(activity.getTaskDate() > now &&
                        activity.getTaskDate() < fiveMins &&
                            activity.getFiveReminder() &&
                                !activity.isNotifiedFive()){
                    notify(activity, "5");
                }


                Long fifteenMins = Polygon.getDateTimezoneMins(15, activity.getTimeZone());
                if(activity.getTaskDate() > now &&
                        activity.getTaskDate() < fifteenMins &&
                            activity.getFifteenReminder() &&
                                !activity.isNotifiedFifteen()){
                    notify(activity, "15");
                }


                Long thirtyMins = Polygon.getDateTimezoneMins(30, activity.getTimeZone());
                if(activity.getTaskDate() > now &&
                        activity.getTaskDate() < thirtyMins &&
                            activity.getThirtyReminder() &&
                                !activity.isNotifiedThirty()){
                    notify(activity, "30");
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    protected void notify(ProspectActivity activity, String mins){
        new Thread(() -> {
            if (activity.getPhones() != null &&
                    !activity.getPhones().equals("")) {
                String[] phones = activity.getPhones().split(",");
                StringBuilder notification = new StringBuilder();
                notification.append("Polygon :: ");
                notification.append(activity.getName() + " in " + mins + " mins with ");
                Prospect prospect = prospectRepo.get(activity.getProspectId());
                notification.append(prospect.getName());
                for(String phone: phones) {
                    smsService.send(phone, notification.toString());
                }
                if(mins.equals("5"))
                    activity.setNotifiedFive(true);
                if(mins.equals("15"))
                    activity.setNotifiedFifteen(true);
                if(mins.equals("30"))
                    activity.setNotifiedThirty(true);

                prospectRepo.updateActivity(activity);
            }
        }).start();
    }

}
