/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Book;
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
        String query = "SELECT nd.MaNguoiDung, nd.TenDangNhap, nd.MatKhau, nd.HoTen, nd.Email, qh.MaQuyenHan " +
                       "FROM NguoiDung nd " +
                       "LEFT JOIN NguoiDungQuyenHan ndqh ON nd.MaNguoiDung = ndqh.MaNguoiDung " +
                       "LEFT JOIN QuyenHan qh ON ndqh.MaQuyenHan = qh.MaQuyenHan " +
                       "WHERE nd.TenDangNhap = ? AND nd.MatKhau = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                List<String> roles = new ArrayList<>();
                boolean userFound = false;

                while (rs.next()) {
                    if (!userFound) { // Chỉ khởi tạo user một lần
                        user = new User();
                        user.setUserID(rs.getString("MaNguoiDung"));
                        user.setUserName(rs.getString("TenDangNhap"));
                        user.setPassword(rs.getString("MatKhau"));
                        user.setFullName(rs.getString("HoTen"));
                        user.setEmail(rs.getString("Email"));
                        userFound = true;
                    }
                    String role = rs.getString("MaQuyenHan");
                    if (role != null && !roles.contains(role)) {
                        roles.add(role);
                    }
                }

                if (userFound) {
                    user.setRoles(roles); // Thiết lập danh sách quyền hạn (roles) cho đối tượng User
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đăng nhập người dùng: " + username, e);
            // Có thể hiển thị thông báo lỗi cho người dùng hoặc ghi log chi tiết hơn
        }
        return user;
    }

    public List<User> searchUser(String searchTerm) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM NguoiDung WHERE MaNguoiDung LIKE ? OR TenDangNhap LIKE ? OR MatKhau LIKE ? OR HoTen LIKE ? OR Email LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm sách với từ khóa: " + searchTerm, e);
        }
        return users;
    }
    
    public boolean resetPassword(String userId, String newPassword) throws SQLException {
        String query = "UPDATE NguoiDung SET MatKhau = ? WHERE MaNguoiDung = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPassword); // Giả định newPassword đã được hash nếu cần
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đặt lại mật khẩu cho người dùng " + userId + ": " + e.getMessage(), e);
            throw e; // Ném lại ngoại lệ để lớp gọi xử lý
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM NguoiDung";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = mapUserFromResultSet(rs);
                user.setRoles(getRolesForUser(user.getUserID())); // Lấy quyền hạn cho từng người dùng
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả người dùng", e);
        }
        return users;
    }

    public User getUserById(String userId) {
        User user = null;
        String query = "SELECT * FROM NguoiDung WHERE MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapUserFromResultSet(rs);
                    user.setRoles(getRolesForUser(user.getUserID())); // Lấy quyền hạn
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy người dùng theo ID: " + userId, e);
        }
        return user;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE NguoiDung SET TenDangNhap = ?, MatKhau = ?, HoTen = ?, Email = ?, Activated = ? WHERE MaNguoiDung = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Bắt đầu giao dịch để đảm bảo tính toàn vẹn dữ liệu

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(6, user.getUserID());

            int rowsAffected = stmt.executeUpdate();

            // Cập nhật quyền hạn: xóa tất cả quyền cũ và thêm lại quyền mới
            deleteUserRoles(user.getUserID());
            insertUserRoles(user.getUserID(), user.getRoles());

            conn.commit(); // Hoàn tất giao dịch
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật người dùng: " + user.getUserID(), e);
            // Rollback nếu có lỗi
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi rollback giao dịch", ex);
            }
            return false;
        }
    }

 
    public boolean deleteUser(String userId) {
        String query = "DELETE FROM NguoiDung WHERE MaNguoiDung = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Bắt đầu giao dịch

            deleteUserRoles(userId); // Xóa quyền hạn trước khi xóa người dùng
            stmt.setString(1, userId);
            int rowsAffected = stmt.executeUpdate();

            conn.commit(); // Hoàn tất giao dịch
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa người dùng: " + userId, e);
            // Rollback nếu có lỗi
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi rollback giao dịch", ex);
            }
            return false;
        }
    }

    
    public boolean addUser(User user) {
        String query = "INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhau, HoTen, Email, Activated) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Bắt đầu giao dịch

            String newUserId = UUID.randomUUID().toString(); // Tạo ID mới duy nhất
            user.setUserID(newUserId); // Gán ID mới cho đối tượng user

            stmt.setString(1, user.getUserID());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());

            int rowsAffected = stmt.executeUpdate();

            // Thêm quyền hạn cho người dùng mới nếu có
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                insertUserRoles(user.getUserID(), user.getRoles());
            }

            conn.commit(); // Hoàn tất giao dịch
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm người dùng: " + user.getUserName(), e);
            // Rollback nếu có lỗi
            try (Connection conn = DBConnection.getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi rollback giao dịch", ex);
            }
            return false;
        }
    }


    public List<String> getRolesForUser(String userId) {
        List<String> roles = new ArrayList<>();
        String query = "SELECT QH.TenQuyenHan FROM QuyenHan QH " +
                       "JOIN NguoiDungQuyenHan NDQH ON QH.MaQuyenHan = NDQH.MaQuyenHan " +
                       "WHERE NDQH.MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("TenQuyenHan"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy quyền hạn cho người dùng: " + userId, e);
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
                stmt.addBatch(); // Thêm vào batch để thực hiện hiệu quả hơn
            }

            stmt.executeBatch(); // Thực thi tất cả các lệnh trong batch

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm quyền hạn cho người dùng: " + userId, e);
        }
    }

    private void deleteUserRoles(String userId) {
        String query = "DELETE FROM NguoiDungQuyenHan WHERE MaNguoiDung = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa quyền hạn người dùng: " + userId, e);
        }
    }

    public User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getString("MaNguoiDung"));
        user.setUserName(rs.getString("TenDangNhap"));
        user.setPassword(rs.getString("MatKhau"));
        user.setFullName(rs.getString("HoTen"));
        user.setEmail(rs.getString("Email"));
        return user;
    }
}
