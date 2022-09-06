package io.shape.assist;

import io.kakai.security.DatabaseAccess;
import io.shape.model.User;
import io.shape.repo.UserRepo;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Element;
import java.util.Set;

@Element
public class SecurityAccess implements DatabaseAccess {

    @Bind
    UserRepo userRepo;

    public String getPassword(String phone){
        User user = userRepo.getPhone(phone);
        if(user != null){
            return user.getPassword();
        }
        return "";
    }

    public Set<String> getRoles(String phone){
        User user = userRepo.getPhone(phone);
        Set<String> roles = userRepo.getUserRoles(user.getId());
        return roles;
    }

    public Set<String> getPermissions(String phone){
        User user = userRepo.getPhone(phone);
        Set<String> permissions = userRepo.getUserPermissions(user.getId());
        return permissions;
    }

}
