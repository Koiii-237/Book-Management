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
import com.bookmanagement.model.Setting;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingDAO {
    private static final Logger LOGGER = Logger.getLogger(SettingDAO.class.getName());

    /**
     * Thêm một cài đặt mới vào cơ sở dữ liệu.
     *
     * @param setting Đối tượng Setting cần thêm.
     * @return true nếu thêm thành công, ngược lại là false.
     * @throws SQLException nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public boolean addSetting(Setting setting) throws SQLException {
        String query = "INSERT INTO settings (setting_name, setting_value) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, setting.getSettingName());
            stmt.setString(2, setting.getSettingValue());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm cài đặt mới: " + setting.getSettingName(), e);
            throw e;
        }
    }

    /**
     * Cập nhật giá trị của một cài đặt hiện có.
     *
     * @param setting Đối tượng Setting chứa thông tin cần cập nhật.
     * @return true nếu cập nhật thành công, ngược lại là false.
     * @throws SQLException nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public boolean updateSetting(Setting setting) throws SQLException {
        String query = "UPDATE settings SET setting_value = ? WHERE setting_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, setting.getSettingValue());
            stmt.setString(2, setting.getSettingName());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật cài đặt: " + setting.getSettingName(), e);
            throw e;
        }
    }

    /**
     * Xóa một cài đặt khỏi cơ sở dữ liệu dựa trên tên cài đặt.
     *
     * @param settingName Tên của cài đặt cần xóa.
     * @return true nếu xóa thành công, ngược lại là false.
     * @throws SQLException nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public boolean deleteSetting(String settingName) throws SQLException {
        String query = "DELETE FROM settings WHERE setting_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, settingName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa cài đặt: " + settingName, e);
            throw e;
        }
    }

    /**
     * Lấy một cài đặt từ cơ sở dữ liệu dựa trên tên cài đặt.
     *
     * @param settingName Tên của cài đặt cần lấy.
     * @return Đối tượng Setting nếu tìm thấy, ngược lại là null.
     * @throws SQLException nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public Setting getSettingByName(String settingName) throws SQLException {
        String query = "SELECT * FROM settings WHERE setting_name = ?";
        Setting setting = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, settingName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    setting = mapSettingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy cài đặt theo tên: " + settingName, e);
            throw e;
        }
        return setting;
    }

    /**
     * Lấy tất cả các cài đặt từ cơ sở dữ liệu.
     *
     * @return Danh sách các đối tượng Setting.
     * @throws SQLException nếu có lỗi xảy ra khi tương tác với cơ sở dữ liệu.
     */
    public List<Setting> getAllSettings() throws SQLException {
        List<Setting> settings = new ArrayList<>();
        String query = "SELECT * FROM settings";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                settings.add(mapSettingFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả cài đặt", e);
            throw e;
        }
        return settings;
    }

    /**
     * Ánh xạ (map) một ResultSet thành một đối tượng Setting.
     *
     * @param rs ResultSet chứa dữ liệu từ cơ sở dữ liệu.
     * @return Đối tượng Setting đã được ánh xạ.
     * @throws SQLException nếu có lỗi khi đọc dữ liệu từ ResultSet.
     */
    private Setting mapSettingFromResultSet(ResultSet rs) throws SQLException {
        Setting setting = new Setting();
        setting.setSettingId(rs.getInt("setting_id"));
        setting.setSettingName(rs.getString("setting_name"));
        setting.setSettingValue(rs.getString("setting_value"));
        return setting;
    }
}
