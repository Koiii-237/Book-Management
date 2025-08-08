/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.Date; // Import java.sql.Date

/**
 * Lớp DAO để thao tác với bảng DonHang.
 */
public class OrderDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    /**
     * Chèn một đơn hàng mới vào cơ sở dữ liệu.
     *
     * @param order Đối tượng Order cần chèn.
     * @return true nếu chèn thành công, false nếu ngược lại.
     */
    public boolean insertOrder(Order order) {
        // Cập nhật SQL để bao gồm cột 'PhuongThucThanhToan'
        String sql = "INSERT INTO DonHang (MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai, PhuongThucThanhToan) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, order.getOrderID());
            ps.setDate(2, Date.valueOf(order.getOrderDate()));
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getCustomerID());
            ps.setString(5, order.getStatus());
            ps.setString(6, order.getPaymentMethod()); // Thêm phương thức thanh toán
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chèn đơn hàng: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy danh sách tất cả các đơn hàng từ cơ sở dữ liệu.
     *
     * @return Danh sách các đơn hàng.
     */
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai, PhuongThucThanhToan FROM DonHang";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Order(
                        rs.getString("MaDonHang"),
                        rs.getDate("NgayDatHang").toLocalDate(),
                        rs.getBigDecimal("TongTien"),
                        rs.getString("MaKH"),
                        rs.getString("TrangThai"),
                        rs.getString("PhuongThucThanhToan") // Lấy phương thức thanh toán
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Tìm kiếm đơn hàng theo tiêu chí.
     *
     * @param search Chuỗi tìm kiếm.
     * @return Danh sách đơn hàng phù hợp.
     */
    public List<Order> searchOrder(String search) {
        List<Order> list = new ArrayList<>();
        // Cập nhật câu truy vấn để tìm kiếm trên PhuongThucThanhToan
        String sql = "SELECT * FROM DonHang WHERE MaDonHang LIKE ? OR MaKH LIKE ? OR TrangThai LIKE ? OR PhuongThucThanhToan LIKE ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchTerm = "%" + search + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            ps.setString(4, searchTerm); // Thêm tìm kiếm theo PhuongThucThanhToan
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Order(
                            rs.getString("MaDonHang"),
                            rs.getDate("NgayDatHang").toLocalDate(),
                            rs.getBigDecimal("TongTien"),
                            rs.getString("MaKH"),
                            rs.getString("TrangThai"),
                            rs.getString("PhuongThucThanhToan") // Lấy phương thức thanh toán
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm đơn hàng: " + search, e);
        }
        return list;
    }

    /**
     * Cập nhật trạng thái của một đơn hàng.
     *
     * @param orderId ID của đơn hàng cần cập nhật.
     * @param newStatus Trạng thái mới của đơn hàng.
     * @return true nếu cập nhật thành công, false nếu ngược lại.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu.
     */
    public boolean updateOrderStatus(String orderId, String newStatus) throws SQLException {
        String sql = "UPDATE DonHang SET TrangThai = ? WHERE MaDonHang = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái đơn hàng: " + orderId, ex);
            throw ex;
        }
    }
    
    /**
     * Lấy một đơn hàng từ cơ sở dữ liệu dựa trên ID đơn hàng.
     *
     * @param orderId ID của đơn hàng cần lấy.
     * @return Đối tượng Order nếu tìm thấy, null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu.
     */
    public Order getOrderByOrderId(String orderId) throws SQLException {
        Order order = null;
        // Cập nhật SQL để chọn đơn hàng theo MaDonHang, bao gồm cả cột PhuongThucThanhToan.
        String sql = "SELECT MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai, PhuongThucThanhToan FROM DonHang WHERE MaDonHang = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId); // Đặt tham số MaDonHang
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Tạo đối tượng Order từ kết quả truy vấn
                    order = new Order(
                        rs.getString("MaDonHang"),
                        rs.getDate("NgayDatHang").toLocalDate(),
                        rs.getBigDecimal("TongTien"),
                        rs.getString("MaKH"),
                        rs.getString("TrangThai"),
                        rs.getString("PhuongThucThanhToan") // Lấy phương thức thanh toán
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo ID: " + orderId, ex);
            throw ex; // Ném ngoại lệ để phương thức gọi xử lý
        }
        return order;
    }

    /**
     * Lấy các gợi ý tìm kiếm dựa trên từ khóa.
     * Tìm kiếm trong MaDonHang, MaKH, TrangThai, PhuongThucThanhToan.
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách các chuỗi gợi ý duy nhất.
     */
    public List<String> getSuggestions(String keyword) {
        Set<String> suggestions = new HashSet<>();
        String sql = "SELECT MaDonHang, MaKH, TrangThai, PhuongThucThanhToan FROM DonHang " +
                     "WHERE MaDonHang LIKE ? OR MaKH LIKE ? OR TrangThai LIKE ? OR PhuongThucThanhToan LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            ps.setString(3, searchTerm);
            ps.setString(4, searchTerm);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(rs.getString("MaDonHang"));
                    suggestions.add(rs.getString("MaKH"));
                    suggestions.add(rs.getString("TrangThai"));
                    suggestions.add(rs.getString("PhuongThucThanhToan"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy gợi ý tìm kiếm: " + keyword, e);
        }
        // Lọc bỏ các giá trị null hoặc rỗng và trả về danh sách
        List<String> filteredSuggestions = new ArrayList<>();
        for (String s : suggestions) {
            if (s != null && !s.trim().isEmpty()) {
                filteredSuggestions.add(s);
            }
        }
        return filteredSuggestions;
    }
}
