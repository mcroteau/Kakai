package io.shape.service;

import io.kakai.security.SecurityManager;
import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Service;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.shape.Polygon;
import io.shape.model.*;
import io.shape.repo.ProspectRepo;
import io.shape.repo.RoleRepo;
import io.shape.repo.UserRepo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Bind
    Kakai kakai;

    @Bind
    UserRepo userRepo;

    @Bind
    RoleRepo roleRepo;

    @Bind
    ProspectRepo prospectRepo;

    @Bind
    AuthService authService;

    @Bind
    SmsService smsService;


    private String getPermission(String id){
        return Polygon.USER_MAINTENANCE + id;
    }

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

    public String create(HttpResponse resp) {
        if (!authService.isAuthenticated()) {
            return "[redirect]/";
        }
        if (!authService.isAdministrator()) {
            resp.set("message", "You must be a super user in order to access users.");
            return "[redirect]/";
        }
        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        resp.set("users", "active");
        resp.set("title", "Create User");
        return "/pages/user/create.jsp";
    }


    public String getUsers(HttpResponse resp){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        if(!authService.isAdministrator()){
            resp.set("message", "You must be a super user in order to access users.");
            return "[redirect]/";
        }
        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));
        resp.set("prospectActivities", prospectActivities);

        List<User> users = userRepo.getList();
        resp.set("usersHref", "active");

        resp.set("users", users);
        resp.set("title", "Users");
        return "/pages/user/index.jsp";
    }

    public String save(HttpResponse resp, HttpRequest req){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        if(!authService.isAdministrator()){
            resp.set("message", "You must be a super user in order to access users.");
            return "[redirect]/";
        }

        User user = (User) kakai.get(req, User.class);
        String phone = Polygon.getPhone(user.getPhone());
        User storedUser = userRepo.getPhone(phone);
        if(storedUser != null){
            resp.set("message", "Someone already exists with the same phone number. Please try a different number.");
            return "[redirect]/users/create";
        }

        user.setPhone(phone);
        String password = SecurityManager.hash(user.getPassword());
        user.setPassword(password);
        userRepo.save(user);

        User savedUser = userRepo.getSaved();

        Long id = savedUser.getId();
        String permission = getPermission(Long.toString(id));
        userRepo.savePermission(id, permission);

        Role role = roleRepo.get(Polygon.USER_ROLE);
        userRepo.saveUserRole(role.getId(), id);

        resp.set("message", "Successfully added user!");
        return "[redirect]/users/edit/" + id;
    }

    public String getEditUser(Long id, HttpResponse resp){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        String permission = getPermission(Long.toString(id));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        List<ProspectActivity> prospectActivities = prospectRepo.getActivities();
        prospectActivities.stream().forEach((prospectActivity -> setPretty(prospectActivity)));

        User user = userRepo.get(id);

        resp.set("usersHref", "active");
        resp.set("user", user);
        resp.set("prospectActivities", prospectActivities);

        return "/pages/user/edit.jsp";
    }


    public String editPassword(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        String permission = getPermission(Long.toString(id));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        User user = userRepo.get(id);
        resp.set("user", user);

        return "/pages/user/password.jsp";
    }


    public String updatePassword(User user, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        String permission = getPermission(Long.toString(user.getId()));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        if(user.getPassword().length() < 7){
            resp.set("message", "Passwords must be at least 7 characters long.");
            return "[redirect]/signup";
        }

        if(!user.getPassword().equals("")){
            user.setPassword(SecurityManager.hash(user.getPassword()));
            userRepo.updatePassword(user);
        }

        resp.set("message", "password successfully updated");
        Long id = authService.getUser().getId();
        return "[redirect]/user/edit_password/" + id;

    }

    public String deleteUser(Long id, HttpResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        if(!authService.isAdministrator()){
            resp.set("message", "You don't have permission");
            return "[redirect]/";
        }

        resp.set("message", "Successfully deleted user");
        return "[redirect]/admin/users";
    }


    public String sendReset(HttpResponse resp, HttpRequest req) {

        try {
            String phone = Polygon.getPhone(req.value("phone"));
            User user = userRepo.getPhone(phone);
            if (user == null) {
                resp.set("message", "We were unable to find user with given cell phone number.");
                return ("[redirect]/user/reset");
            }

            String guid = Polygon.getString(6);
            user.setPassword(SecurityManager.hash(guid));
            userRepo.update(user);

            String message = "Atto >_ Your temporary password is " + guid;
            smsService.send(user.getPhone(), message);

        }catch(Exception e){
            e.printStackTrace();
            resp.set("message", "Something went awry! You might need to contact support!");
            return "[redirect]/signin";
        }

        resp.set("message", "Successfully sent reset password!");
        return "[redirect]/signin";
    }

    public String resetPassword(Long id, HttpResponse resp, HttpRequest req) {

        User user = userRepo.get(id);
        String uuid = req.value("uuid");
        String username = req.value("username");
        String rawPassword = req.value("password");

        if(rawPassword.length() < 7){
            resp.set("message", "Passwords must be at least 7 characters long.");
            return "[redirect]/user/confirm?username=" + username + "&uuid=" + uuid;
        }

        if(!rawPassword.equals("")){
            String password = SecurityManager.hash(rawPassword);
            user.setPassword(password);
            userRepo.updatePassword(user);
        }

        resp.set("message", "Password successfully updated");
        return "/pages/user/success.jsp";
    }

    public String updateUser(Long id, HttpResponse resp, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = getPermission(Long.toString(id));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        User user = userRepo.get(id);
        String originalPhone = user.getPhone();
        String phone = Polygon.getPhone(req.value("phone"));

        user.setPhone(phone);
        userRepo.update(user);

        if(!phone.equals(originalPhone)){
            authService.signout();
            resp.set("message", "Successfully updated user. Your cell changed to " + phone + ". Please sign in with your new cell.");
            return "[redirect]/signin";
        }else{
            resp.set("message", "Successfully updated user.");
            return "[redirect]/users/edit/" + id;
        }
    }
}
