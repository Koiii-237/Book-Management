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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RevenueDAO {
    
    private static final Logger LOGGER = Logger.getLogger(RevenueDAO.class.getName());
    
    public List<Map<String, Object>> getDetailedSalesData(LocalDate startDate, LocalDate endDate, String category) throws SQLException {
        List<Map<String, Object>> salesData = new ArrayList<>();
        String sql = "SELECT od.order_id, b.title, b.category, od.quantity, od.price_at_sale, o.order_date, o.discount " +
                     "FROM dbo.order_details od " +
                     "JOIN dbo.orders o ON od.order_id = o.order_id " +
                     "JOIN dbo.books b ON od.book_id = b.book_id " +
                     "WHERE o.order_date BETWEEN ? AND ? ";

        // Thêm điều kiện lọc theo danh mục nếu không phải là "Tất cả danh mục"
        if (category != null && !category.equals("Tất cả danh mục")) {
            sql += "AND b.category = ? ";
        }
        
        sql += "ORDER BY o.order_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            if (category != null && !category.equals("Tất cả danh mục")) {
                stmt.setString(3, category);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("orderId", rs.getInt("order_id"));
                    record.put("bookTitle", rs.getString("title"));
                    record.put("category", rs.getString("category"));
                    record.put("quantity", rs.getInt("quantity"));
                    record.put("priceAtSale", rs.getBigDecimal("price_at_sale"));
                    record.put("orderDate", rs.getObject("order_date", LocalDate.class));
                    record.put("discount", rs.getBigDecimal("discount"));
                    salesData.add(record);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu", e);
            throw e;
        }
        return salesData;
    }
}
