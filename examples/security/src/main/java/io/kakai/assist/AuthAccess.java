package io.kakai.assist;

import io.kakai.annotate.Bind;
import io.kakai.model.User;
import io.kakai.UserRepo;
import io.kakai.annotate.Element;
import io.kakai.security.DatabaseAccess;

import java.util.Set;

@Element
public class AuthAccess implements DatabaseAccess {

    @Bind
    UserRepo userRepo;

    public User getUser(String credential){
        User user = userRepo.getPhone(credential);
        if(user == null){
            user = userRepo.getEmail(credential);
        }
        return user;
    }

    public String getPassword(String credential){
        User user = getUser(credential);
        if(user != null) return user.getPassword();
        return "";
    }

    public Set<String> getRoles(String credential){
        User user = getUser(credential);
        Set<String> roles = userRepo.getUserRoles(user.getId());
        return roles;
    }

    public Set<String> getPermissions(String credential){
        User user = getUser(credential);
        Set<String> permissions = userRepo.getUserPermissions(user.getId());
        return permissions;
    }

}
