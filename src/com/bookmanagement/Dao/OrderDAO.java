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
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.*;
import java.util.List;

/**
 *
 * @author ADMIN
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
        // SQL để chèn dữ liệu vào bảng DonHang, bao gồm cả cột TrangThai.
        String sql = "INSERT INTO DonHang (MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, order.getOrderID()); // Đặt mã đơn hàng
            ps.setDate(2, Date.valueOf(order.getOrderDate())); // Đặt ngày đặt hàng
            ps.setBigDecimal(3, order.getTotalAmount()); // Đặt tổng tiền
            ps.setString(4, order.getCustomerID()); // Đặt mã khách hàng
            ps.setString(5, order.getStatus()); // Đặt trạng thái đơn hàng
            int rowsAffected = ps.executeUpdate(); // Thực thi câu lệnh
            return rowsAffected > 0; // Trả về true nếu có hàng bị ảnh hưởng (chèn thành công)
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm đơn hàng: " + order.getOrderID(), ex);
            ex.printStackTrace();
            return false;
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
        // SQL để chọn đơn hàng theo MaDonHang, bao gồm cả cột TrangThai.
        String sql = "SELECT MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai FROM DonHang WHERE MaDonHang = ?";
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
                        rs.getString("TrangThai")
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
     * Lấy tất cả các đơn hàng từ cơ sở dữ liệu.
     *
     * @return Danh sách các đối tượng Order.
     */
    public List<Order> getAllOrders() {
        List<Order> l = new ArrayList<>();
        // SQL để lấy tất cả đơn hàng, sắp xếp theo ngày đặt hàng giảm dần.
        String sql = "SELECT MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai FROM DonHang ORDER BY NgayDatHang DESC";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // Thêm từng đơn hàng vào danh sách
                l.add(new Order(
                    rs.getString("MaDonHang"),
                    rs.getDate("NgayDatHang").toLocalDate(),
                    rs.getBigDecimal("TongTien"),
                    rs.getString("MaKH"),
                    rs.getString("TrangThai")
                ));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả đơn hàng", ex);
            ex.printStackTrace();
        }
        return l;
    }

    /**
     * Tìm kiếm đơn hàng trong cơ sở dữ liệu dựa trên từ khóa.
     * Tìm kiếm theo MaDonHang, TrangThai hoặc MaKH.
     *
     * @param search Từ khóa tìm kiếm.
     * @return Danh sách các đối tượng Order phù hợp với từ khóa.
     */
    public ArrayList<Order> searchOrder(String search) {
        ArrayList<Order> list = new ArrayList<>();
        // SQL để tìm kiếm đơn hàng với các điều kiện LIKE cho MaDonHang, TrangThai và MaKH.
        String sql = "SELECT MaDonHang, NgayDatHang, TongTien, MaKH, TrangThai FROM DonHang WHERE LOWER(MaDonHang) LIKE ? OR LOWER(TrangThai) LIKE ? OR LOWER(MaKH) LIKE ? ORDER BY NgayDatHang DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + search.toLowerCase() + "%"; // Tạo chuỗi tìm kiếm dạng "%keyword%"
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Thêm đơn hàng tìm thấy vào danh sách
                    list.add(new Order(
                        rs.getString("MaDonHang"),
                        rs.getDate("NgayDatHang").toLocalDate(),
                        rs.getBigDecimal("TongTien"),
                        rs.getString("MaKH"),
                        rs.getString("TrangThai")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm đơn hàng: " + search, e);
            e.printStackTrace();
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
}
