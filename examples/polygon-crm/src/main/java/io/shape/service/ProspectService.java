package io.shape.service;


import io.kakai.annotate.Bind;
import io.kakai.annotate.Service;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.shape.Polygon;
import io.shape.model.*;
import io.shape.repo.ActivityRepo;
import io.shape.repo.EffortRepo;
import io.shape.repo.ProspectRepo;
import io.shape.repo.StatusRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProspectService {

    @Bind
    AuthService authService;

    @Bind
    ProspectRepo prospectRepo;

    @Bind
    StatusRepo statusRepo;

    @Bind
    EffortRepo effortRepo;

    @Bind
    ActivityRepo activityRepo;

    public void setPretty(ProspectActivity prospectActivity){
        try {
            Prospect prospect = prospectRepo.get(prospectActivity.getProspectId());
            prospectActivity.setProspectName(prospect.getName());
            SimpleDateFormat format = new SimpleDateFormat(Polygon.DATE_TIME);
            Date date = format.parse(Long.toString(prospectActivity.getTaskDate()));

            SimpleDateFormat formatter = new SimpleDateFormat(Polygon.DATE_PRETTY);
            String pretty = formatter.format(date);
            prospectActivity.setPrettyTime(pretty);
        }catch (Exception ex){}
    }


    private ProspectActivity checkCorrectPhonesFormat(String phones, ProspectActivity prospectActivity) {
        if(phones == null ||
                phones.equals("")){
            return prospectActivity;
        }
        String[] phoneParts = phones.split(",");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(String phone: phoneParts){
            phone = Polygon.getPhone(phone);
            sb.append(phone);
            if(count < phoneParts.length){
                sb.append(",");
            }
            count++;
        }
        prospectActivity.setPhones(sb.toString());
        return prospectActivity;
    }

    protected ProspectActivity setPhone(ProspectActivity activity){
        User user = authService.getUser();
        if(user.getPhone() != null &&
                !user.getPhone().equals("")){
            activity.setPhones(user.getPhone());
        }
        return activity;
    }

    public String data() {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Map<String, Integer> activitiesAggregated = new HashMap<>();
        List<ProspectActivity> salesActivities = prospectRepo.getCompletedSalesActivities();
        System.out.println("sales activities " + salesActivities.size());
        for(ProspectActivity prospectActivity : salesActivities){
            try {
                if(prospectActivity.getCompleteDate() > 0){
                    SimpleDateFormat incoming = new SimpleDateFormat(Polygon.DATE_FORMAT);
                    Date datePre = incoming.parse(prospectActivity.getCompleteDate().toString());

                    SimpleDateFormat outgoing = new SimpleDateFormat("yyyy-MM-dd");
                    String dateKey = outgoing.format(datePre);

                    System.out.println("key " + dateKey);
                    if(!activitiesAggregated.containsKey(dateKey)){
                        activitiesAggregated.put(dateKey, 1);
                    }else{
                        int count = activitiesAggregated.get(dateKey);
                        count++;
                        activitiesAggregated.replace(dateKey, count);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Date,# Sales\n");
        for(Map.Entry<String, Integer> entry: activitiesAggregated.entrySet()){
            sb.append(entry.getKey() + "," + entry.getValue()+ "\n");
        }

        return sb.toString();
    }

    public String snapshot(HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        /*
            Conversion Rate
            How many active Sales Activities
            How many completed Sales Activities
            How many prospects
            How many propsects by status
         */
        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        BigDecimal salesCount = new BigDecimal(prospectRepo.getCompletedSalesActivitiesCount());
        BigDecimal completedCount = new BigDecimal(prospectRepo.getCompletedActivitiesCount());

        BigDecimal conversionRate = new BigDecimal(0);
        try{
            conversionRate = salesCount.divide(completedCount, 3, RoundingMode.HALF_UP).movePointRight(2);
        }catch(Exception ex){}

        Long activeCount = prospectRepo.getActiveActivitiesCount();
        Long prospectsCount = prospectRepo.getCount();

        resp.set("salesCount", salesCount);
        resp.set("prospectsCount", prospectsCount);
        resp.set("completedCount", completedCount);
        resp.set("activeCount", activeCount);
        resp.set("conversionRate", conversionRate);
        resp.set("snapshotHref", "active");

        resp.set("title", "Snapshot");
        return "/pages/prospect/snapshot.jsp";
    }

    public String searchScreen(HttpRequest req, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        List<Prospect> prospects = prospectRepo.getList();

        resp.set("searchHref", "active");
        resp.set("prospects", prospects);
        resp.set("title", "Search Prospects");
        return "/pages/prospect/search.jsp";
    }

    public String getProspects(HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        String query = req.value("q");
        List<Prospect> prospects = prospectRepo.getResults(query);
        resp.set("prospects", prospects);

        resp.set("searchHref", "active");
        resp.set("title", "Search Results");
        return "/pages/prospect/results.jsp";
    }

    public String index(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        List<Status> statuses = statusRepo.getList();
        Prospect prospect = prospectRepo.get(id);

        List<ProspectActivity> upcomingActivities = prospectRepo.getIncompletedActivities(id);
        upcomingActivities.stream().forEach((prospectActivity) -> setPretty(prospectActivity));
        Effort effort = effortRepo.getProspectEffort(prospect.getId(), false);

        resp.set("effort", effort);
        System.out.println("effort:" + effort);
        resp.set("upcomingActivities", upcomingActivities);
        resp.set("statuses", statuses);
        resp.set("prospect", prospect);

        resp.set("title", prospect.getName());
        return "/pages/prospect/index.jsp";
    }

    public String create(HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        List<Status> statuses = statusRepo.getList();
        resp.set("statuses", statuses);

        resp.set("createHref", "active");
        resp.set("title", "Create Prospect");
        return "/pages/prospect/create.jsp";
    }

    public String save(HttpResponse resp, HttpRequest req) {

        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String name = req.value("name");
        String phone = Polygon.getPhone(req.value("phone"));
        String email = Polygon.getSpaces(req.value("email"));
        String contacts = req.value("contacts");
        Long statusId = Long.parseLong(req.value("status"));

        if(name.equals("")){
            resp.set("message", "Please give your prospect a name...");
            return "[redirect]/prospects/create";
        }

        Prospect prospect = new Prospect();
        prospect.setName(name);
        prospect.setPhone(phone);
        prospect.setEmail(email);
        prospect.setContacts(contacts);
        prospect.setStatusId(statusId);

        Prospect savedProspect = prospectRepo.save(prospect);

        resp.set("message", "Successfully saved " + prospect.getName() + "!");
        return "[redirect]/prospects/" + savedProspect.getId();
    }

    public String getEdit(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        List<Status> statuses = statusRepo.getList();
        Prospect prospect = prospectRepo.get(id);
        Effort effort = effortRepo.getProspectEffort(prospect.getId(), false);

        resp.set("effort", effort);
        resp.set("statuses", statuses);
        resp.set("prospect", prospect);

        resp.set("title", "Edit Prospect");
        return "/pages/prospect/edit.jsp";
    }

    public String update(HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Long id = Long.parseLong(req.value("id"));
        String name = req.value("name");
        String phone = req.value("phone");
        String email = req.value("email");
        String contacts = req.value("contacts");
        Long statusId = Long.parseLong(req.value("statusId"));

        if(name.equals("")){
            resp.set("message", "Please give your prospect a name...");
            return "[redirect]/prospects/edit/" + id;
        }

        Prospect prospect = prospectRepo.get(id);
        prospect.setName(name);
        prospect.setPhone(phone);
        prospect.setEmail(email);
        prospect.setContacts(contacts);
        prospect.setStatusId(statusId);
        prospectRepo.update(prospect);

        List<Status> statuses = statusRepo.getList();

        resp.set("statuses", statuses);
        resp.set("message", "Successfully updated prospect");
        return "[redirect]/prospects/edit/" + prospect.getId();
    }


    public String delete(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        if(!authService.isAdministrator()){
            return "[redirect]/unauthorized";
        }

        prospectRepo.deleteActivities(id);
        prospectRepo.delete(id);
        resp.set("message", "Successfully deleted prospect.");

        return "[redirect]/prospects";
    }

    public String addActivity(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        List<Activity> activities = activityRepo.getList();
        Prospect prospect = prospectRepo.get(id);
        resp.set("timezone", TimeZone.getDefault().getDisplayName());
        resp.set("activities", activities);
        resp.set("prospect", prospect);

        resp.set("title", "Prospect Activity");
        return "/pages/prospect_activity/index.jsp";
    }

    public String saveActivity(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        try {

            Long activityId = Long.parseLong(req.value("activity-id"));
            Activity activity = activityRepo.get(activityId);
            Prospect prospect = prospectRepo.get(id);
            Effort effort = effortRepo.getProspectEffort(prospect.getId(), false);

            String date = req.value("activity-date");
            String hour = Polygon.pad(req.value("activity-hour").trim(), 2, "0");
            String minute = Polygon.pad(req.value("activity-minute").trim(), 2, "0");

            String dateStr = date.concat(" ")
                    .concat(hour)
                    .concat(":").concat(minute);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date datePreParsed = format.parse(dateStr);

            Calendar cal = Calendar.getInstance();
            cal.setTime(datePreParsed);

            String timezone = req.value("timezone");

            Date dateMillis = new Date();
            dateMillis.setTime(cal.getTimeInMillis());
            SimpleDateFormat sdf = new SimpleDateFormat(Polygon.DATE_FORMAT);

            Long taskDate = Long.parseLong(sdf.format(dateMillis));

            ProspectActivity prospectActivity = new ProspectActivity();
            prospectActivity.setTaskDate(taskDate);
            prospectActivity.setTimeZone(timezone);
            prospectActivity.setActivityId(activityId);
            prospectActivity.setProspectId(id);

            if(effort != null){
                prospectActivity.setEffortId(effort.getId());
            }

            setPhone(prospectActivity);

            prospectRepo.saveActivity(prospectActivity);

        }catch(Exception ex){
            ex.printStackTrace();
            resp.set("message", "You may have entered in an incorrect time. Please try again");
            return "[redirect]/prospects/activity/add/" + id;
        }

        ProspectActivity savedActivity = prospectRepo.getLastInsertedActivity();
        resp.set("message", "Successfully added sales action");
        return "[redirect]/prospects/activity/edit/"  + savedActivity.getId();
    }

    public String completeActivity(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        User user = authService.getUser();
        ProspectActivity prospectActivity = prospectRepo.getActivity(id);
        prospectActivity.setCompleteDate(Polygon.getDate());
        prospectActivity.setCompletedByUserId(user.getId());
        prospectRepo.setCompleted(prospectActivity);

        resp.set("message", "Successfully completed activity.");
        return "[redirect]/prospects/history/" + prospectActivity.getProspectId();
    }

    public String deleteActivity(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        ProspectActivity activity = prospectRepo.getActivity(id);
        prospectRepo.deleteActivity(id);
        resp.set("message", "Successfully deleted activity");
        return "[redirect]/prospects/" + activity.getProspectId();
    }


    public String saveEffort(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Prospect prospect = prospectRepo.get(id);
        Effort effort = new Effort();
        effort.setProspectId(prospect.getId());
        effort.setStartDate(Polygon.getDate());
        effort.setStartingStatusId(prospect.getStatusId());
        effortRepo.save(effort);

        resp.set("message", "Successfully started effort. All sales activities will be recorded and a clear view at your sales funnels will be visible.");
        return "[redirect]/prospects/" + id;
    }

    public String stopEffort(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        Prospect prospect = prospectRepo.get(id);
        Effort effort = effortRepo.getProspectEffort(id, false);
        effort.setEndDate(Polygon.getDate());
        effort.setEndingStatusId(prospect.getStatusId());
        effort.setFinished(true);
        Status endingStatus = statusRepo.get(prospect.getStatusId());
        if(endingStatus.equals(Polygon.CUSTOMER_STATUS)){
            effort.setSuccess(true);
        }
        effortRepo.update(effort);
        resp.set("message", "Successfully stopped effort.");
        return "[redirect]/prospects/" + id;
    }

    public String history(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));

        Prospect prospect = prospectRepo.get(id);
        List<ProspectActivity> activities = prospectRepo.getCompletedActivities(id);
        activities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));

        resp.set("prospectActivities", prospectActivities);
        resp.set("prospect", prospect);
        resp.set("activities", activities);

        resp.set("title", "Prospect History");
        return "/pages/prospect/history.jsp";
    }

    public String editActivity(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));

        ProspectActivity activity = prospectRepo.getActivity(id);
        setPretty(activity);

        resp.set("activity", activity);
        resp.set("prospectActivities", prospectActivities);

        resp.set("title", "Edit Activity");
        return "/pages/prospect_activity/edit.jsp";
    }

    public String updateActivity(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        ProspectActivity prospectActivity = prospectRepo.getActivity(id);
        String phones = req.value("phones");
        checkCorrectPhonesFormat(phones, prospectActivity);

        Boolean five = getBooleanNotification("five", req);
        Boolean fifteen = getBooleanNotification("fifteen", req);
        Boolean thirty = getBooleanNotification("thirty", req);

        prospectActivity.setCompleted(false);
        prospectActivity.setFiveReminder(five);
        prospectActivity.setFifteenReminder(fifteen);
        prospectActivity.setThirtyReminder(thirty);

        prospectRepo.updateActivity(prospectActivity);

        resp.set("message", "Successfully updated sales activity");
        return "[redirect]/prospects/activity/edit/" + id;
    }

    public String editNotes(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Prospect prospect = prospectRepo.get(id);
        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));

        resp.set("prospect", prospect);
        resp.set("prospectActivities", prospectActivities);

        resp.set("title", "Notes");
        return "/pages/prospect/notes.jsp";
    }

    public String updateNotes(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Prospect prospect = prospectRepo.get(id);
        String notes = req.value("notes");
        prospect.setNotes(notes);
        prospectRepo.update(prospect);

        resp.set("message", "Successfully updated notes!");
        return "[redirect]/prospects/notes/edit/" + id;
    }

    public String markSale(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authUser = authService.getUser();
        List<ProspectActivity> prospectActivities = prospectRepo.getIncompletedActivities(id);
        for(ProspectActivity prospectActivity : prospectActivities){
            Activity activity = activityRepo.get("Sale");
            prospectActivity.setCompleted(true);
            prospectActivity.setActivityId(activity.getId());
            prospectRepo.markAsSale(prospectActivity);

            prospectActivity.setCompleteDate(Polygon.getDate());
            prospectActivity.setCompletedByUserId(authUser.getId());
            prospectRepo.setCompleted(prospectActivity);
        }
        resp.set("message", "Congratulations! Successfully updated all sales activities to sale!");
        return "[redirect]/prospects/edit/" + id;
    }

    public Boolean getBooleanNotification(String parameter, HttpRequest req){
        String on = req.value(parameter);
        if(on != null &&
                on.equals("on")){
            return true;
        }
        return false;
    }
}
