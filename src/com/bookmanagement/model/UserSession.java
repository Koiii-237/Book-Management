package com.bookmanagement.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class UserSession {
    
    
     private static final Logger LOGGER = Logger.getLogger(UserSession.class.getName());
    private static UserSession instance;
    private User currentUser;
    private Set<String> permissions;

    private UserSession(User user, Set<String> permissions) {
        this.currentUser = user;
        this.permissions = permissions;
        LOGGER.info("UserSession created for user: " + user.getUsername());
    }

    public static void startSession(User user, Set<String> permissions) {
        if (instance == null) {
            instance = new UserSession(user, permissions);
        } else {
            instance.currentUser = user;
            instance.permissions = permissions;
            LOGGER.info("UserSession updated for user: " + user.getUsername());
        }
    }

    public static void endSession() {
        if (instance != null) {
            instance.currentUser = null;
            instance.permissions = null;
            instance = null;
            LOGGER.info("UserSession ended.");
        }
    }

    public static User getCurrentUser() {
        return (instance != null) ? instance.currentUser : null;
    }

    public static boolean isLoggedIn() {
        return instance != null && instance.currentUser != null;
    }
    
    public static boolean hasPermission(String permission) {
        return isLoggedIn() && instance.permissions.contains(permission);
    }
    
    // Getter
    public static Set<String> getPermissions() {
        return (isLoggedIn() && instance.permissions != null) ? instance.permissions : new HashSet<>();
    }
}
