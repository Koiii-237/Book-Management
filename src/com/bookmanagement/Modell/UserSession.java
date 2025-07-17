package com.bookmanagement.Modell;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class UserSession {
    private String userId;
    private String username;
    private String userName; // Tên hiển thị đầy đủ của người dùng (ví dụ: "Nguyễn Thị Thu")
    private Set<String> permissions; // Tập hợp các mã quyền hạn mà người dùng này có

    public UserSession(String userId, String username, String userName, Set<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.userName = userName;
        // Sử dụng Collections.unmodifiableSet để đảm bảo tập hợp quyền không bị sửa đổi từ bên ngoài
        this.permissions = Collections.unmodifiableSet(new HashSet<>(permissions));
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserName() {
        return userName;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    // Phương thức tiện ích để kiểm tra nhanh một quyền cụ thể
    public boolean hasPermission(String permissionCode) {
        return permissions.contains(permissionCode);
    }
}