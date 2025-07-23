/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // Create
    public User login(String username, String password) {
        User user = null;
        String query = "SELECT * FROM NguoiDung WHERE TenDangNhap = ? AND MatKhau = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUserFromResultSet(rs);

                    // Đảm bảo người dùng đã được kích hoạt
                    if (!user.isActivated()) {
                        return null;
                    }

                    // Gán danh sách quyền
                    List<String> roles = getUserRoles(user.getUserID());
                    user.setRoles(roles);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    // Thêm người dùng mới
    public boolean addUser(User user) {
        String query = "INSERT INTO NguoiDung (TenDangNhap, MatKhau, HoTen, Email, IsActivated) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setBoolean(6, user.isActivated());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        String userId = rs.getString(1);
                        insertUserRoles(userId, user.getRoles());
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật người dùng
    public boolean updateUser(User user) {
        String query = "UPDATE NguoiDung SET TenDangNhap = ?, MatKhau = ?, HoTen = ?, Email = ?, IsActivated = ? WHERE MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setBoolean(6, user.isActivated());
            stmt.setString(7, user.getUserID());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                deleteUserRoles(user.getUserID());
                insertUserRoles(user.getUserID(), user.getRoles());
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa người dùng
    public boolean deleteUser(int userId) {
        String deleteUserRoles = "DELETE FROM NguoiDungQuyenHan WHERE MaQuyenHan = ?";
        String deleteUser = "DELETE FROM NguoiDung WHERE MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(deleteUserRoles);
             PreparedStatement stmt2 = conn.prepareStatement(deleteUser)) {

            stmt1.setInt(1, userId);
            stmt1.executeUpdate();

            stmt2.setInt(1, userId);
            int affectedRows = stmt2.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Lấy toàn bộ người dùng
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM NguoiDung";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = mapUserFromResultSet(rs);
                user.setRoles(getUserRoles(user.getUserID()));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // Lấy người dùng theo ID
    public User getUserById(String userId) {
        String query = "SELECT * FROM NguoiDung WHERE MaNguoiDung = ?";
        User user = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUserFromResultSet(rs);
                    user.setRoles(getUserRoles(userId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    // -------------------------------------
    // Các phương thức phụ trợ
    // -------------------------------------

    public List<String> getUserRoles(String userId) {
        List<String> roles = new ArrayList<>();
        String query = "SELECT MaQuyenHan FROM NguoiDungQuyenHan WHERE MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("MaQuyenHan"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }

    private void insertUserRoles(String userId, List<String> roles) {
        String query = "INSERT INTO NguoiDungQuyenHan (MaNguoiDung, MaQuyenHan) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (String role : roles) {
                stmt.setString(1, userId);
                stmt.setString(2, role);
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUserRoles(String userId) {
        String query = "DELETE FROM NguoiDungQuyenHan WHERE MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getString("MaNguoiDung"));
        user.setUserName(rs.getString("TenDangNhap"));
        user.setPassword(rs.getString("MatKhau"));
        user.setFullName(rs.getString("HoTen"));
        user.setEmail(rs.getString("Email"));
        user.setActivated(rs.getBoolean("IsActivated"));
        return user;
    }
}
