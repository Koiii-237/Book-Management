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
     public static UserSession login(String username, String password) {
        UserDAO userDAO = new UserDAO();
        // bước 1: xác thực user
        User user = userDAO.login(username, password);
        if (user == null) return null;
        // bước 2: lấy danh sách quyền của user
        List<String> roles = userDAO.getUserRoles(user.getUserID());
        // bước 3: khởi tạo UserSession với user và roles
        return new UserSession(user, roles);
    }
}
