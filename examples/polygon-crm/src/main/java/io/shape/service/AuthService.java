package io.shape.service;

import io.kakai.security.SecurityManager;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Service;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.shape.Polygon;
import io.shape.model.*;
import io.shape.repo.UserRepo;

@Service
public class AuthService {

    @Bind
    UserRepo userRepo;

    public boolean signin(String phone, String password){
        User user = userRepo.getPhone(phone);
        if(user == null) {
            return false;
        }
        return SecurityManager.signin(phone, password);
    }

    public boolean signout(){
        return SecurityManager.signout();
    }

    public boolean isAuthenticated(){
        return SecurityManager.isAuthenticated();
    }

    public boolean isAdministrator(){
        return SecurityManager.hasRole(Polygon.SUPER_ROLE);
    }

    public boolean hasPermission(String permission){
        return SecurityManager.hasPermission(permission);
    }

    public boolean hasRole(String role){
        return SecurityManager.hasRole(role);
    }

    public User getUser(){
        String phone = SecurityManager.getUser();
        User user = userRepo.getPhone(phone);
        return user;
    }

    public String authenticate(HttpRequest req, HttpResponse resp) {
        try{
            String phone = Polygon.getPhone(req.value("phone"));
            String password = req.value("password");
            if(!signin(phone, password)){
                resp.set("message", "Wrong phone and password");
                return "[redirect]/signin";
            }

            User authdUser = userRepo.getPhone(phone);

            req.getSession().set("username", authdUser.getName());
            req.getSession().set("userId", authdUser.getId());

        } catch ( Exception e ) {
            e.printStackTrace();
            resp.set("message", "Something is a little off. We apologize. Give is a ring.");
            return "[redirect]/";
        }

        return "[redirect]/";
    }

    public String deAuthenticate(HttpRequest req, HttpResponse resp) {
        signout();
        resp.set("message", "Successfully signed out");
        return "[redirect]/";
    }
}
