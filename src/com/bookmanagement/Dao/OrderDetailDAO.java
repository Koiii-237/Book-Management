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
public class OrderDetailDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDetailDAO.class.getName());

    public boolean insertOrderDetail(OrderItem orderDetail) {
        if (orderDetail == null || orderDetail.getOrderID() == null || orderDetail.getBookID() == null) {
            throw new IllegalArgumentException("Thông tin chi tiết đơn hàng không hợp lệ");
        }
        String sql = "INSERT INTO ChiTietDonHang (MaCTDH, MaDH, MaSach, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, orderDetail.getOrderDetailID());
            ps.setString(2, orderDetail.getOrderID());
            ps.setString(3, orderDetail.getBookID());
            ps.setInt(4, orderDetail.getQuantity());
            ps.setBigDecimal(5, orderDetail.getUnitPrice());
            ps.setBigDecimal(6, orderDetail.getSubtotal());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm chi tiết đơn hàng: " + orderDetail.getOrderDetailID(), ex);
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách chi tiết đơn hàng dựa trên ID đơn hàng.
     *
     * @param orderId ID của đơn hàng.
     * @return Danh sách các đối tượng OrderDetail.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy vấn cơ sở dữ
     * liệu.
     */
    public List<OrderItem> getOrderDetailByOrderId(String orderId) throws SQLException {
        List<OrderItem> details = new ArrayList<>();
        // SQL để lấy chi tiết đơn hàng, kết hợp với bảng Sach để lấy TenSach.
        String sql = "SELECT od.MaCTDH, od.MaDH, od.MaSach, od.SoLuong, od.DonGia, od.ThanhTien, b.TenSach "
                + "FROM ChiTietDonHang od JOIN Sach b ON od.MaSach = b.MaSach WHERE od.MaDH = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new OrderItem(
                            rs.getString("MaCTDH"),
                            rs.getInt("SoLuong"),
                            rs.getBigDecimal("DonGia"),
                            rs.getBigDecimal("ThanhTien"),
                            rs.getString("MaDH"),
                            rs.getString("MaSach"),
                            rs.getString("TenSach") // Lấy tên sách từ bảng Sach
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy chi tiết đơn hàng theo ID đơn hàng: " + orderId, ex);
            throw ex;
        }
        return details;
    }

    /**
     * Xóa tất cả chi tiết đơn hàng cho một đơn hàng cụ thể. Thường được gọi
     * trước khi thêm lại các chi tiết mới khi cập nhật đơn hàng.
     *
     * @param orderId Mã đơn hàng.
     * @return true nếu xóa thành công, false nếu có lỗi.
     */
    public boolean deleteOrderDetailsByOrderId(String orderId) {
        String query = "DELETE FROM ChiTietDonHang WHERE MaDH = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa chi tiết đơn hàng cho Order ID: " + orderId, e);
            return false;
        }
    }

    /**
     * Ánh xạ (map) một hàng từ ResultSet sang đối tượng OrderDetail.
     *
     * @param rs ResultSet chứa dữ liệu chi tiết đơn hàng.
     * @return Đối tượng OrderDetail được tạo từ dữ liệu ResultSet.
     * @throws SQLException Nếu có lỗi khi truy cập dữ liệu từ ResultSet.
     */
    private OrderItem mapOrderDetailFromResultSet(ResultSet rs) throws SQLException {
        OrderItem detail = new OrderItem();
        detail.setOrderDetailID(rs.getString("MaCTDH"));
        detail.setQuantity(rs.getInt("SoLuong"));
        detail.setUnitPrice(rs.getBigDecimal("DonGia"));
        detail.setSubtotal(rs.getBigDecimal("ThanhTien"));
        detail.setOrderID(rs.getString("MaDH"));
        detail.setBookID(rs.getString("MaSach"));
        detail.setBookName(rs.getString("TenSach")); // Lấy tên sách từ JOIN
        return detail;
    }
}
