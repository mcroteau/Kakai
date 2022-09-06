package io.kakai.security;

import java.util.Set;

public interface DatabaseAccess {
    /**
     * Intended to return the user's password based
     * on the username
     *
     * @param user
     * @return returns hashed password
     */
    public String getPassword(String user);


    /**
     * takes a username
     *
     * @param user
     * @return returns a unique set of role strings
     */
    public Set<String> getRoles(String user);


    /**
     *
     * @param user
     * @return returns a unique set of user permissions
     * example permission user:maintenance:(id) (id)
     * replaced with actual id of user
     */
    public Set<String> getPermissions(String user);

}

