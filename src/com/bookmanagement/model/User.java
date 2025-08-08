/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ADMIN
 */
public class User {
    private String userID;
    private String userName;
    private String password;
    private String fullName;
    private String email;
    private List<String> roles;

    public User() {
        this.roles = new ArrayList<>();
    }

    public User(String userID, String userName, String password, String fullName, String email, List<String> roles) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    // Getters và Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public boolean hasPermission(String permission) {
        // Kiểm tra xem danh sách quyền hạn (roles) có null không
        // và kiểm tra xem nó có chứa quyền hạn (permission) được yêu cầu không.
        return this.roles != null && this.roles.contains(permission);
    }
}
