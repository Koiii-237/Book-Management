package com.bookmanagement.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class UserSession {

    private static User currentUser;
    private String role;
    private List<String> permissions;

    public UserSession(User user, List<String> permissions) {
        currentUser = user;
        this.permissions = permissions;
    }

    public static void startSession(User user) {
        currentUser = user;
    }

    // Truy xuất người dùng hiện tại
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public User getUser() {
        return currentUser;
    }
    
    // Kết thúc phiên đăng nhập
    public static void endSession() {
        currentUser = null;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    // Kiểm tra có người dùng đang đăng nhập không
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
