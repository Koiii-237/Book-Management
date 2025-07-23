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
import com.bookmanagement.model.Customer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.sql.*;

public class CustomerDAO {

    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());

    public String generateNextId() {
        
        String id = null;
        String sql = "SELECT MAX(MaKH) FROM KhachHang WHERE MaKH LIKE 'KH%'";
        try (Connection conn = DBConnection.getConnection(); 
                PreparedStatement pstmt = conn.prepareStatement(sql); 
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                id = rs.getString(1);
            }

            int nextNumber = 1;
            if (id != null && id.matches("KH\\d{3,}")) {
                try {
                    String numPart = id.substring(2);
                    nextNumber = Integer.parseInt(numPart) + 1;
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Lỗi khi phân tích số từ MaKhachHang: " + id + ". Bắt đầu lại từ KH001.", e);
                    nextNumber = 1;
                }
            }
            return String.format("KH%03d", nextNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi sinh mã khách hàng tiếp theo: " + e.getMessage(), e);
            return "KH_ERROR_" + System.currentTimeMillis();
        }
    }
    
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO KhachHang (MaKH, TenKH, DiaChi, SDT) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String id = customer.getId();
            if (id == null || id.isEmpty()) {
                id = generateNextId();
                customer.setId(id);
            }
            
            pstmt.setString(1, customer.getId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { 
                LOGGER.log(Level.WARNING, "Lỗi: Mã khách hàng '" + customer.getId() + "' đã tồn tại.", e);
                JOptionPane.showMessageDialog(null, "The customer code existed " + customer.getId() + " Please enter other code.", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else {
                LOGGER.log(Level.SEVERE, "Lỗi khi thêm khách hàng: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Error when add customer code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }
    
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE KhachHang SET TenKH = ?, DiaChi = ?, SDT = ? WHERE MaKH = ?"; // Đã bỏ Email
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getId()); // Chỉ số đã thay đổi

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật khách hàng: " + e.getMessage(), e);
            return false;
        }
    }
    
    public boolean deleteCustomer(String id) {
        String sql = "DELETE FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa khách hàng: " + e.getMessage(), e);
            return false;
        }
    }
    
    public Customer getCustomerById(String id) {
        String sql = "SELECT * FROM KhachHang WHERE MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                    rs.getString("MaKH"),
                    rs.getString("TenKH"),
                    rs.getString("DiaChi"),
                    rs.getString("SDT")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy khách hàng theo ID: " + e.getMessage(), e);
        }
        return null;
    }
    
    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY TenKH ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("MaKH"),
                    rs.getString("TenKH"),
                    rs.getString("DiaChi"),
                    rs.getString("SDT")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả khách hàng: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return customers;
    }
    
    public ArrayList<Customer> searchCustomers(String search) {
        ArrayList<Customer> customers = new ArrayList<>();
        // Đã bỏ Email khỏi điều kiện tìm kiếm
        String sql = "SELECT * FROM KhachHang WHERE LOWER(MaKH) LIKE ? OR LOWER(TenKH) LIKE ? OR LOWER(DiaChi) LIKE ? OR LOWER(SDT) LIKE ? ORDER BY TenKhachHang ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + search.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern); 
            pstmt.setString(4, searchPattern); 

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("MaKH"),
                    rs.getString("TenKH"),
                    rs.getString("DiaChi"),
                    rs.getString("SDT")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm khách hàng: " + e.getMessage(), e);
        }
        return customers;
    }
    
    
}
