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
        LOGGER.info("UserSession created for user: " + user.getUserName());
    }

    // Phương thức tĩnh để khởi tạo phiên làm việc.
    public static void startSession(User user, Set<String> permissions) {
        if (instance == null) {
            instance = new UserSession(user, permissions);
        } else {
            // Cập nhật lại phiên làm việc nếu đã tồn tại
            instance.currentUser = user;
            instance.permissions = permissions;
            LOGGER.info("UserSession updated for user: " + user.getUserName());
        }
    }

    // Phương thức tĩnh để kết thúc phiên làm việc.
    public static void endSession() {
        if (instance != null) {
            instance.currentUser = null;
            instance.permissions = null;
            instance = null;
            LOGGER.info("UserSession ended.");
        }
    }

    // Truy xuất người dùng hiện tại
    public static User getCurrentUser() {
        return (instance != null) ? instance.currentUser : null;
    }

    // Kiểm tra có người dùng đang đăng nhập không
    public static boolean isLoggedIn() {
        return instance != null && instance.currentUser != null;
    }
    
    // Kiểm tra quyền hạn của người dùng
    public static boolean hasPermission(String permission) {
        return isLoggedIn() && instance.permissions.contains(permission);
    }
    
    // Lấy tất cả quyền hạn
    public static Set<String> getPermissions() {
        return (isLoggedIn() && instance.permissions != null) ? instance.permissions : new HashSet<>();
    }
}
