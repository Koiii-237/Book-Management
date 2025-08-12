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
import com.bookmanagement.model.Promotion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PromotionDAO {
    private static final Logger LOGGER = Logger.getLogger(PromotionDAO.class.getName());

    /**
     * Thêm một khuyến mãi mới vào cơ sở dữ liệu.
     *
     * @param promotion Đối tượng Promotion cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi.
     */
    public boolean addPromotion(Promotion promotion) {
        String sql = "INSERT INTO dbo.promotions (code, discount_percentage, start_date, end_date, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, promotion.getCode());
            stmt.setBigDecimal(2, promotion.getDiscountPercentage());
            stmt.setObject(3, promotion.getStartDate());
            stmt.setObject(4, promotion.getEndDate());
            stmt.setBoolean(5, promotion.isActive());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        promotion.setPromotionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm khuyến mãi: " + promotion.getCode(), e);
        }
        return false;
    }
    
    public List<Promotion> getAllActivePromotions() throws SQLException {
        List<Promotion> promotions = new ArrayList<>();
        // SQL query để lấy các khuyến mãi đang hoạt động (active = true)
        // và ngày hiện tại nằm trong khoảng [start_date, end_date]
        String sql = "SELECT * FROM dbo.promotions WHERE is_active = 1 AND start_date <= GETDATE() AND end_date >= GETDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                promotions.add(mapPromotionFromResultSet(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách khuyến mãi đang hoạt động.", ex);
            throw ex;
        }
        return promotions;
    }

    /**
     * Cập nhật thông tin khuyến mãi.
     *
     * @param promotion Đối tượng Promotion với thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updatePromotion(Promotion promotion) {
        String sql = "UPDATE dbo.promotions SET code = ?, discount_percentage = ?, start_date = ?, end_date = ?, is_active = ? WHERE promotion_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, promotion.getCode());
            stmt.setBigDecimal(2, promotion.getDiscountPercentage());
            stmt.setObject(3, promotion.getStartDate());
            stmt.setObject(4, promotion.getEndDate());
            stmt.setBoolean(5, promotion.isActive());
            stmt.setInt(6, promotion.getPromotionId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật khuyến mãi ID: " + promotion.getPromotionId(), e);
            return false;
        }
    }

    /**
     * Xóa một khuyến mãi khỏi cơ sở dữ liệu.
     *
     * @param promotionId ID của khuyến mãi cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi.
     */
    public boolean deletePromotion(int promotionId) {
        String sql = "DELETE FROM dbo.promotions WHERE promotion_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, promotionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa khuyến mãi ID: " + promotionId, e);
            return false;
        }
    }

    /**
     * Lấy một khuyến mãi theo ID.
     *
     * @param promotionId ID của khuyến mãi.
     * @return Đối tượng Promotion nếu tìm thấy, ngược lại trả về null.
     */
    public Promotion getPromotionById(int promotionId) {
        String sql = "SELECT * FROM dbo.promotions WHERE promotion_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, promotionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPromotionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy khuyến mãi theo ID: " + promotionId, e);
        }
        return null;
    }

    /**
     * Lấy một khuyến mãi theo mã code.
     *
     * @param code Mã code của khuyến mãi.
     * @return Đối tượng Promotion nếu tìm thấy, ngược lại trả về null.
     */
    public Promotion getPromotionByCode(String code) {
        String sql = "SELECT * FROM dbo.promotions WHERE code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPromotionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy khuyến mãi theo code: " + code, e);
        }
        return null;
    }

    /**
     * Lấy tất cả các khuyến mãi.
     *
     * @return Một danh sách các đối tượng Promotion.
     */
    public List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM dbo.promotions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                promotions.add(mapPromotionFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả khuyến mãi", e);
        }
        return promotions;
    }

    /**
     * Lấy tất cả các khuyến mãi đang hoạt động.
     *
     * @return Một danh sách các đối tượng Promotion đang hoạt động.
     */
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        // Truy vấn này kiểm tra cả is_active và ngày hiện tại nằm trong khoảng start_date và end_date
        String sql = "SELECT * FROM dbo.promotions WHERE is_active = 1 AND GETDATE() BETWEEN start_date AND end_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                promotions.add(mapPromotionFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả khuyến mãi đang hoạt động", e);
        }
        return promotions;
    }
    
    
    public void autoUpdatePromotionStatus() {
        String sql = "UPDATE dbo.promotions SET is_active = 0 WHERE end_date < ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            int updatedRows = stmt.executeUpdate();
            if (updatedRows > 0) {
                LOGGER.log(Level.INFO, "Đã cập nhật trạng thái của {0} khuyến mãi hết hạn.", updatedRows);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái khuyến mãi tự động", e);
            try {
                throw e;
            } catch (SQLException ex) {
                Logger.getLogger(PromotionDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    public boolean isPromotionActive(Promotion promotion) {
        if (promotion == null || !promotion.isActive()) {
            return false;
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = promotion.getStartDate();
        LocalDate endDate = promotion.getEndDate();

        // Kiểm tra xem ngày hiện tại có nằm trong khoảng thời gian khuyến mãi không
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }
    
    
    public boolean isPromotionActive(int promotionId, LocalDate checkDate) {
        Promotion promotion = getPromotionById(promotionId);
        if (promotion == null || !promotion.isActive()) {
            return false;
        }
        LocalDate startDate = promotion.getStartDate();
        LocalDate endDate = promotion.getEndDate();
        // Kiểm tra xem ngày checkDate có nằm trong khoảng thời gian khuyến mãi không
        return !checkDate.isBefore(startDate) && !checkDate.isAfter(endDate);
    }
    
    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng Promotion.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng promotions.
     * @return Một đối tượng Promotion đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Promotion mapPromotionFromResultSet(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(rs.getInt("promotion_id"));
        promotion.setCode(rs.getString("code"));
        promotion.setDiscountPercentage(rs.getBigDecimal("discount_percentage"));
        promotion.setStartDate(rs.getObject("start_date", LocalDate.class));
        promotion.setEndDate(rs.getObject("end_date", LocalDate.class));
        promotion.setActive(rs.getBoolean("is_active"));
        return promotion;
    }
}
