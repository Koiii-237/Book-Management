/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

/**
 *
 * @author ADMIN
 */
import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Permission;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
public class PermissionDAO {
    private static final Logger LOGGER = Logger.getLogger(PermissionDAO.class.getName());

    /**
     * Thêm một quyền hạn mới vào cơ sở dữ liệu.
     *
     * @param permission Đối tượng Permission cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi.
     */
    public boolean addPermission(Permission permission) {
        String sql = "INSERT INTO dbo.permissions (permission_name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, permission.getPermissionName());
            stmt.setString(2, permission.getDescription());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        permission.setPermissionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm quyền hạn: " + permission.getPermissionName(), e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin quyền hạn.
     *
     * @param permission Đối tượng Permission với thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updatePermission(Permission permission) {
        String sql = "UPDATE dbo.permissions SET permission_name = ?, description = ? WHERE permission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, permission.getPermissionName());
            stmt.setString(2, permission.getDescription());
            stmt.setInt(3, permission.getPermissionId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật quyền hạn ID: " + permission.getPermissionId(), e);
            return false;
        }
    }

    /**
     * Xóa một quyền hạn khỏi cơ sở dữ liệu.
     *
     * @param permissionId ID của quyền hạn cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi.
     */
    public boolean deletePermission(int permissionId) {
        String sql = "DELETE FROM dbo.permissions WHERE permission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, permissionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa quyền hạn ID: " + permissionId, e);
            return false;
        }
    }

    /**
     * Lấy một quyền hạn theo ID.
     *
     * @param permissionId ID của quyền hạn.
     * @return Đối tượng Permission nếu tìm thấy, ngược lại trả về null.
     */
    public Permission getPermissionById(int permissionId) {
        String sql = "SELECT * FROM dbo.permissions WHERE permission_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, permissionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPermissionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy quyền hạn theo ID: " + permissionId, e);
        }
        return null;
    }

    /**
     * Lấy một quyền hạn theo tên.
     *
     * @param permissionName Tên của quyền hạn.
     * @return Đối tượng Permission nếu tìm thấy, ngược lại trả về null.
     */
    public Permission getPermissionByName(String permissionName) {
        String sql = "SELECT * FROM dbo.permissions WHERE permission_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, permissionName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPermissionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy quyền hạn theo tên: " + permissionName, e);
        }
        return null;
    }

    /**
     * Lấy tất cả các quyền hạn trong cơ sở dữ liệu.
     *
     * @return Một danh sách các đối tượng Permission.
     */
    public List<Permission> getAllPermissions() {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM dbo.permissions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                permissions.add(mapPermissionFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả quyền hạn", e);
        }
        return permissions;
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng Permission.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng permissions.
     * @return Một đối tượng Permission đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Permission mapPermissionFromResultSet(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setPermissionId(rs.getInt("permission_id"));
        permission.setPermissionName(rs.getString("permission_name"));
        permission.setDescription(rs.getString("description"));
        return permission;
    }
}
