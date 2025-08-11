package com.bookmanagement.service;

import com.bookmanagement.Dao.UserDAO;
import com.bookmanagement.model.User;
import com.bookmanagement.model.UserSession;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ADMIN
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    
    private final UserDAO userDAO;

    /**
     * Hàm khởi tạo.
     */
    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Phương thức đăng nhập người dùng.
     * Phương thức này sẽ khởi tạo một UserSession mới.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return true nếu đăng nhập thành công, ngược lại trả về false.
     */
    public boolean login(String username, String password) {
        User user = userDAO.login(username, password);
        if (user != null) {
            // Lấy danh sách quyền hạn từ User.getRoles()
            // Sau đó chuyển đổi nó sang một HashSet để khớp với yêu cầu của UserSession
            HashSet<String> userPermissions = new HashSet<>(user.getPermissions());
            
            // Sử dụng phương thức startSession mới để truyền cả user và permissions (dạng Set)
            UserSession.startSession(user, userPermissions);
            
            LOGGER.log(Level.INFO, "Người dùng đã đăng nhập thành công: {0}", user.getUsername());
            return true;
        } else {
            LOGGER.log(Level.WARNING, "Đăng nhập thất bại cho người dùng: {0}", username);
            return false;
        }
    }

    /**
     * Phương thức đăng xuất người dùng.
     * Phương thức này sẽ kết thúc phiên làm việc hiện tại.
     */
    public void logout() {
        UserSession.endSession();
        LOGGER.log(Level.INFO, "Người dùng đã đăng xuất.");
    }
}
