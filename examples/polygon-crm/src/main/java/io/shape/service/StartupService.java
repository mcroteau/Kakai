package io.shape.service;

import io.kakai.security.SecurityManager;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Service;
import io.shape.Polygon;
import io.shape.assist.SecurityAccess;
import io.shape.jobs.NotificationJob;
import io.shape.model.*;
import io.shape.repo.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


import java.util.List;
import java.util.TimeZone;

@Service
public class StartupService {

    @Bind
    UserRepo userRepo;

    @Bind
    RoleRepo roleRepo;

    @Bind
    StatusRepo statusRepo;

    @Bind
    ActivityRepo activityRepo;

    @Bind
    ProspectRepo prospectRepo;

    @Bind
    SecurityAccess securityAccess;

    @Bind
    SmsService smsService;


    public void init() {

        SecurityManager.configure(securityAccess);

        Role superRole = roleRepo.get(Polygon.SUPER_ROLE);
        Role userRole = roleRepo.get(Polygon.USER_ROLE);

        if(superRole == null){
            superRole = new Role();
            superRole.setName(Polygon.SUPER_ROLE);
            roleRepo.save(superRole);
        }

        if(userRole == null){
            userRole = new Role();
            userRole.setName(Polygon.USER_ROLE);
            roleRepo.save(userRole);
        }

        User existing = userRepo.getPhone("9079878652");
        String password = SecurityManager.hash(Polygon.SUPER_PASSWORD);

        superRole = roleRepo.get(Polygon.SUPER_ROLE);

        if(existing == null){
            User superUser = new User();
            superUser.setName("Super User!");
            superUser.setPhone("9079878652");
            superUser.setUsername(Polygon.SUPER_USERNAME);
            superUser.setPassword(password);
            userRepo.save(superUser);
            User savedUser = userRepo.getSaved();
            userRepo.saveUserRole(savedUser.getId(), superRole.getId());
            String permission = Polygon.USER_MAINTENANCE + savedUser.getId();
            userRepo.savePermission(savedUser.getId(), permission);
        }

        Long statusCount = statusRepo.getCount();
        if(statusCount == 0) {
            String[] names = {Polygon.IDLE_STATUS,
                    Polygon.IMPORT_STATUS,
                    Polygon.PROSPECT_STATUS,
                    Polygon.WORKING_STATUS,
                    Polygon.CUSTOMER_STATUS};

            for (String name : names) {
                Status status = new Status();
                status.setName(name);
                statusRepo.save(status);
            }
        }

        Long activityCount = activityRepo.getCount();
        if(activityCount == 0) {
            String[] activityNames = {"Call",
                    "Email",
                    "Mailer",
                    "Meeting",
                    "Demo",
                    "Sale"};
            for (String name : activityNames) {
                Activity activity = new Activity();
                activity.setName(name);
                activityRepo.save(activity);
            }
        }

        try {

            Class[] jobs = { NotificationJob.class };
            String[] jobNames = { Polygon.NOTIFICATION_JOB };
            String[] triggers = { Polygon.NOTIFICATION_TRIGGER };

            for(int n = 0; n < jobs.length; n++){

                JobDetail job = JobBuilder.newJob(jobs[n])
                        .withIdentity(jobNames[n], Polygon.NOTIFICATION_GROUP).build();

                job.getJobDataMap().put(Polygon.PROSPECT_REPO_KEY, prospectRepo);
                job.getJobDataMap().put(Polygon.SMS_SERVICE_KEY, smsService);

                Trigger trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity(triggers[n], Polygon.NOTIFICATION_GROUP)
                        .withSchedule(
                                SimpleScheduleBuilder.simpleSchedule()
                                        .withIntervalInSeconds(Polygon.NOTIFICATION_JOB_DURATION).repeatForever())
                        .build();

                Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.startDelayed(0 );
                JobKey key = new JobKey(jobNames[n], Polygon.NOTIFICATION_GROUP);
                if(!scheduler.checkExists(key)) {
                    scheduler.scheduleJob(job, trigger);
                    System.out.println(jobs[n] + " repeated " + Polygon.NOTIFICATION_JOB_DURATION + " seconds");
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        genData();

    }


    protected void genData(){

        List<Status> statuses = statusRepo.getList();
        Long prospectCount = prospectRepo.getCount();
        if(prospectCount == 0) {
            String[] prospectNames = {"Blue Water Trucking Co.",
                    "Love Hour Meditation",
                    "Jeff's Silly Suds Brew House",
                    "Grand Rapids Auto Park",
                    "Tidy Tim's Bean Factory"};

            for (String name : prospectNames) {
                Status status = statuses.get(Polygon.getNumber(statuses.size()));
                Prospect prospect = new Prospect();
                prospect.setName(name);
                prospect.setEmail(Polygon.getString(13));
                prospect.setPhone("(808) 012-0910");
                prospect.setStatusId(status.getId());
                prospectRepo.save(prospect);
            }
        }

        List<Activity> activities = activityRepo.getList();

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();

        if(prospectActivities.size() == 0) {
            List<Prospect> prospects = prospectRepo.getList();
            for (int z = 1; z < prospects.size(); z++) {
                Prospect prospect = prospects.get(z);
                int idx = Polygon.getNumber(activities.size());
                Activity activity = activities.get(idx);
                ProspectActivity prospectActivity = new ProspectActivity();
                prospectActivity.setActivityId(activity.getId());
                prospectActivity.setProspectId(prospect.getId());
                prospectActivity.setTaskDate(Polygon.getDate());
                prospectActivity.setTimeZone(TimeZone.getDefault().getID());
                prospectRepo.saveActivity(prospectActivity);
            }
        }
    }

    public void addLotsOfMockSalesPlease(Prospect prospect){
        for(int z = 100; z > 0; z--){
            Activity activity = activityRepo.get("Sale");

            int sales = Polygon.getNumber(2017);
            for(int x = sales; x > 0; x--) {
                ProspectActivity prospectActivity = new ProspectActivity();
                prospectActivity.setActivityId(activity.getId());
                prospectActivity.setProspectId(prospect.getId());
                prospectActivity.setTaskDate(Polygon.getDate());
                prospectActivity.setTimeZone(TimeZone.getDefault().getID());
                prospectRepo.saveActivity(prospectActivity);

                ProspectActivity savedProspectActivity = prospectRepo.getLastInsertedActivity();
                Long date = Polygon.getDateTimezoneMins(x * (60 * 24), TimeZone.getDefault().getID());
                savedProspectActivity.setCompleteDate(date);
                savedProspectActivity.setCompleted(true);
                prospectRepo.markAsSale(savedProspectActivity);
            }
        }
    }


    public void shutdown() throws Exception{
        System.out.println("shutdown...");
    }

}
