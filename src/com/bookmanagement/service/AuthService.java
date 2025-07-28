package com.bookmanagement.service;

import com.bookmanagement.Dao.UserDAO;
import com.bookmanagement.model.User;
import com.bookmanagement.model.UserSession;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ADMIN
 */
public class AuthService {
    private static UserDAO userDAO = new UserDAO();
    public static UserSession login(String username, String password) {
        User user = userDAO.login(username, password); // Gọi phương thức login từ UserDAO

        if (user != null) {
            // Lấy danh sách quyền hạn của người dùng từ UserDAO
            List<String> permissions = userDAO.getRolesForUser(user.getUserID());
            UserSession session = new UserSession(user, permissions);
            UserSession.startSession(user); // Bắt đầu session cho người dùng
            return session;
        }
        return null; // Đăng nhập thất bại
    }
    
    public static void logout() {
        UserSession.endSession();
    }
}
