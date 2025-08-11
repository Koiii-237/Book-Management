/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;// Giả sử đã có lớp DBConnection để lấy kết nối
import com.bookmanagement.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ADMIN
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu.
     *
     * @param user Đối tượng User cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi xảy ra.
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO dbo.users (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Lấy user_id tự động được tạo và gán lại cho đối tượng User
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm người dùng mới: " + user.getUsername(), e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin của một người dùng đã tồn tại.
     *
     * @param user Đối tượng User chứa thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm thấy người dùng.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE dbo.users SET username = ?, password_hash = ?, email = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật người dùng có ID: " + user.getUserId(), e);
            return false;
        }
    }

    /**
     * Xóa một người dùng khỏi cơ sở dữ liệu.
     *
     * @param userId ID của người dùng cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi hoặc không tìm thấy người dùng.
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM dbo.users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa người dùng có ID: " + userId, e);
            return false;
        }
    }

    /**
     * Lấy một người dùng dựa trên ID.
     *
     * @param userId ID của người dùng cần tìm.
     * @return Đối tượng User nếu tìm thấy, ngược lại trả về null.
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM dbo.users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy người dùng theo ID: " + userId, e);
        }
        return null;
    }

    /**
     * Lấy một người dùng dựa trên username.
     * Phương thức này rất hữu ích cho chức năng đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng.
     * @return Đối tượng User nếu tìm thấy, ngược lại trả về null.
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM dbo.users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy người dùng theo username: " + username, e);
        }
        return null;
    }

    /**
     * Lấy tất cả người dùng từ cơ sở dữ liệu.
     *
     * @return Một danh sách các đối tượng User.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM dbo.users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả người dùng", e);
        }
        return users;
    }
    
    
    public User login(String username, String password) {
        // SQL query để lấy người dùng theo username
        String sql = "SELECT user_id, username, password_hash, email, created_at FROM dbo.users WHERE username = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password_hash");

                    // So sánh mật khẩu thô với mật khẩu đã lưu trong DB
                    if (password.equals(storedPassword)) {
                        user = mapUserFromResultSet(rs);
                        user.setPermissions(getUserPermissions(user.getUserId()));
                        LOGGER.log(Level.INFO, "Đăng nhập thành công cho người dùng: {0}", username);
                    } else {
                        LOGGER.log(Level.WARNING, "Mật khẩu không đúng cho người dùng: {0}", username);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Không tìm thấy người dùng với tên: {0}", username);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đăng nhập người dùng", e);
        }
        return user;
    }

    public List<String> getUserPermissions(int userId) {
        List<String> permissions = new ArrayList<>();
        // SQL query để lấy tên quyền từ bảng user_permissions và permissions
        String sql = "SELECT p.permission_name FROM dbo.user_permissions up "
                   + "JOIN dbo.permissions p ON up.permission_id = p.permission_id "
                   + "WHERE up.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("permission_name"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy quyền hạn của người dùng", e);
        }
        return permissions;
    }
    
    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng User.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng users.
     * @return Một đối tượng User đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        // Lưu ý: Các trường permissions không có trong bảng users, nó sẽ được xử lý riêng
        return user;
    }
}
