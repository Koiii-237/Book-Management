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
     * Thêm một đơn hàng mới vào cơ sở dữ liệu.
     * Lưu ý: order_date sẽ được tự động tạo bởi database (GETDATE()).
     *
     * @param order Đối tượng Order cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi xảy ra.
     */
    public boolean addOrder(Order order) {
        String sql = "INSERT INTO dbo.orders (customer_id, status, total_amount, total_discount, promotion_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Xử lý các trường có thể NULL
            if (order.getCustomerId() != null) {
                stmt.setInt(1, order.getCustomerId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, order.getStatus());
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setBigDecimal(4, order.getTotalDiscount());
            if (order.getPromotionId() != null) {
                stmt.setInt(5, order.getPromotionId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm đơn hàng mới.", e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin của một đơn hàng đã tồn tại.
     *
     * @param order Đối tượng Order chứa thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm thấy đơn hàng.
     */
    public boolean updateOrder(Order order) {
        String sql = "UPDATE dbo.orders SET customer_id = ?, status = ?, total_amount = ?, total_discount = ?, promotion_id = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Xử lý các trường có thể NULL
            if (order.getCustomerId() != null) {
                stmt.setInt(1, order.getCustomerId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, order.getStatus());
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setBigDecimal(4, order.getTotalDiscount());
            if (order.getPromotionId() != null) {
                stmt.setInt(5, order.getPromotionId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setInt(6, order.getOrderId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật đơn hàng có ID: " + order.getOrderId(), e);
            return false;
        }
    }

    /**
     * Xóa một đơn hàng khỏi cơ sở dữ liệu dựa trên ID.
     *
     * @param orderId ID của đơn hàng cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi hoặc không tìm thấy đơn hàng.
     */
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM dbo.orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa đơn hàng có ID: " + orderId, e);
            return false;
        }
    }

    /**
     * Lấy một đơn hàng dựa trên ID.
     *
     * @param orderId ID của đơn hàng cần tìm.
     * @return Đối tượng Order nếu tìm thấy, ngược lại trả về null.
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM dbo.orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapOrderFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo ID: " + orderId, e);
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }
    
    /**
     * Lấy tất cả các đơn hàng có trong cơ sở dữ liệu.
     *
     * @return Một danh sách các đối tượng Order.
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM dbo.orders";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả đơn hàng.", e);
        }
        return orders;
    }

    /**
     * Lấy danh sách các đơn hàng của một khách hàng cụ thể.
     *
     * @param customerId ID của khách hàng.
     * @return Một danh sách các đơn hàng của khách hàng đó.
     */
    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM dbo.orders WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrderFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy đơn hàng theo ID khách hàng: " + customerId, e);
        }
        return orders;
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng Order.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng đơn hàng.
     * @return Một đối tượng Order đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Order mapOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getObject("customer_id", Integer.class));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setTotalDiscount(rs.getBigDecimal("total_discount"));
        order.setPromotionId(rs.getObject("promotion_id", Integer.class));
        return order;
    }
}
