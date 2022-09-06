package io.kakai.security;

import com.sun.net.httpserver.HttpExchange;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpSession;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityManager {

    static final String USER_KEY        = "user_key";
    public static final String HASH_256 = "SHA-256";

    static DatabaseAccess databaseAccess;
    static Map<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();

    //////// Thank you Apache Shiro! ////////
    private static ThreadLocal<HttpRequest> requestStorage = new InheritableThreadLocal<>();
    private static ThreadLocal<HttpExchange> exchangeStorage = new InheritableThreadLocal<>();

    public static void SAVE(HttpRequest request){
        requestStorage.set(request);
    }
    public static void SAVE(HttpExchange exchange){
        exchangeStorage.set(exchange);
    }

    public static HttpRequest getRequest(){
        return requestStorage.get();
    }
    public static HttpExchange getExchange(){
        return exchangeStorage.get();
    }

    public static boolean hasRole(String role){
        String user = getUser();
        if(user != null) {
            Set<String> roles = databaseAccess.getRoles(user);
            if(roles.contains(role)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermission(String permission){
        String user = getUser();
        if(user != null) {
            Set<String> permissions = databaseAccess.getPermissions(user);
            if(permissions.contains(permission)){
                return true;
            }
        }
        return false;
    }

    public static String getUser(){
        HttpRequest req = SecurityManager.getRequest();
        HttpSession httpSession = req.getSession(false);

        if(httpSession != null){
            String user = (String) httpSession.get(SecurityManager.USER_KEY);
            return user;
        }
        return "";
    }

    public static String get(String key){
        HttpRequest req = SecurityManager.getRequest();
        HttpSession httpSession = req.getSession();
        if(httpSession != null){
            return String.valueOf(httpSession.get(key));
        }
        return "";
    }


    public static boolean set(String key, String value){
        HttpRequest req = SecurityManager.getRequest();
        HttpSession httpSession = req.getSession();
        if(httpSession != null){
            httpSession.set(key, value);
        }
        return true;
    }

    public static boolean signin(String username, String passwordUntouched){
        String hashed = SecurityManager.hash(passwordUntouched);
        String password = databaseAccess.getPassword(username);

        if(!isAuthenticated() &&
                password.equals(hashed)){

            HttpRequest req = SecurityManager.getRequest();

            HttpSession oldHttpSession = req.getSession(false);
            if(oldHttpSession != null){
                oldHttpSession.dispose();
            }

            HttpSession httpSession = req.getSession(true);
            httpSession.set(SecurityManager.USER_KEY, username);
            sessions.put(httpSession.getId(), httpSession);

            return true;
        }

        return false;
    }

    public static boolean signout(){
        HttpRequest req = SecurityManager.getRequest();
        HttpSession httpSession = req.getSession(false);

        if(httpSession != null){
            httpSession.dispose();
            httpSession.remove(SecurityManager.USER_KEY);
            if(sessions.containsKey(httpSession.getId())){
                sessions.remove(httpSession.getId());
            }
        }
        return true;
    }

    public static boolean isAuthenticated(){
        HttpRequest req = SecurityManager.getRequest();
        if(req != null) {
            HttpSession httpSession = req.getSession(false);
            if (httpSession != null && sessions.containsKey(httpSession.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean configure(DatabaseAccess databaseAccess){
        SecurityManager.databaseAccess = databaseAccess;
        return true;
    }

    public static String hash(String password){
        MessageDigest md = null;
        StringBuffer passwordHashed = new StringBuffer();

        try {
            md = MessageDigest.getInstance(SecurityManager.HASH_256);
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                passwordHashed.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordHashed.toString();
    }

}
