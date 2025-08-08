/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.service;

import com.bookmanagement.DBPool.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp dịch vụ để lấy dữ liệu thống kê doanh thu từ cơ sở dữ liệu.
 * @author ADMIN
 */
public class RevenueService {

    private static final Logger LOGGER = Logger.getLogger(RevenueService.class.getName());

    /**
     * Lấy dữ liệu doanh thu theo tháng từ database dựa trên schema mới.
     * @return Map với key là tháng và value là tổng doanh thu.
     */
    public Map<Integer, Double> getMonthlyRevenue(int year) {
        Map<Integer, Double> monthlyRevenueData = new LinkedHashMap<>();

        // Truy vấn dựa trên sơ đồ database đã cung cấp
        String sql = "SELECT MONTH(dh.NgayDatHang) AS month, SUM(ctdh.ThanhTien) AS revenue "
                + "FROM DonHang dh "
                + "JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDH "
                + "WHERE YEAR(dh.NgayDatHang) = ? "
                + "GROUP BY MONTH(dh.NgayDatHang) "
                + "ORDER BY MONTH(dh.NgayDatHang)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                monthlyRevenueData.put(rs.getInt("month"), rs.getDouble("revenue"));
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi nạp dữ liệu doanh thu hàng tháng từ DB", ex);
        }

        return monthlyRevenueData;
    }

}
