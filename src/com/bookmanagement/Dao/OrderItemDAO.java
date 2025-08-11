package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp OrderDetailDAO cung cấp các phương thức để tương tác với bảng
 * ChiTietDonHang (Order Detail) trong cơ sở dữ liệu.
 */
public class OrderItemDAO {

     private static final Logger LOGGER = Logger.getLogger(OrderItemDAO.class.getName());

    /**
     * Thêm một mục đơn hàng mới vào cơ sở dữ liệu.
     *
     * @param orderItem Đối tượng OrderItem cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi xảy ra.
     */
    public boolean addOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO dbo.order_items (order_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItem.getOrderId());
            stmt.setInt(2, orderItem.getBookId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getUnitPrice());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm mục đơn hàng cho order ID: " + orderItem.getOrderId(), e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin của một mục đơn hàng đã tồn tại.
     *
     * @param orderItem Đối tượng OrderItem chứa thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm thấy mục đơn hàng.
     */
    public boolean updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE dbo.order_items SET order_id = ?, book_id = ?, quantity = ?, unit_price = ? WHERE order_item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItem.getOrderId());
            stmt.setInt(2, orderItem.getBookId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getUnitPrice());
            stmt.setInt(5, orderItem.getOrderItemId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật mục đơn hàng có ID: " + orderItem.getOrderItemId(), e);
            return false;
        }
    }

    /**
     * Xóa một mục đơn hàng khỏi cơ sở dữ liệu dựa trên ID.
     *
     * @param orderItemId ID của mục đơn hàng cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi hoặc không tìm thấy mục đơn hàng.
     */
    public boolean deleteOrderItem(int orderItemId) {
        String sql = "DELETE FROM dbo.order_items WHERE order_item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa mục đơn hàng có ID: " + orderItemId, e);
            return false;
        }
    }

    /**
     * Lấy một mục đơn hàng dựa trên ID.
     *
     * @param orderItemId ID của mục đơn hàng cần tìm.
     * @return Đối tượng OrderItem nếu tìm thấy, ngược lại trả về null.
     */
    public OrderItem getOrderItemById(int orderItemId) {
        String sql = "SELECT * FROM dbo.order_items WHERE order_item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapOrderItemFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy mục đơn hàng theo ID: " + orderItemId, e);
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    /**
     * Lấy tất cả các mục đơn hàng cho một đơn hàng cụ thể.
     *
     * @param orderId ID của đơn hàng.
     * @return Một danh sách các đối tượng OrderItem.
     */
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM dbo.order_items WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderItems.add(mapOrderItemFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy các mục đơn hàng cho order ID: " + orderId, e);
        }
        return orderItems;
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng OrderItem.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng order_items.
     * @return Một đối tượng OrderItem đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private OrderItem mapOrderItemFromResultSet(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(rs.getInt("order_item_id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setBookId(rs.getInt("book_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setUnitPrice(rs.getBigDecimal("unit_price"));
        return orderItem;
    }
}
