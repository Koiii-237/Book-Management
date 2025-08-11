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
import com.bookmanagement.model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigDecimal;

public class PaymentDAO {
    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class.getName());

    /**
     * Thêm một bản ghi thanh toán mới vào cơ sở dữ liệu.
     *
     * @param payment Đối tượng Payment cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi.
     */
    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO dbo.payments (order_id, payment_method, transaction_code, amount, status, qr_code_data) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setString(2, payment.getPaymentMethod());
            stmt.setString(3, payment.getTransactionCode());
            stmt.setBigDecimal(4, payment.getAmount());
            stmt.setString(5, payment.getStatus());
            stmt.setString(6, payment.getQrCodeData());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setPaymentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm thanh toán cho đơn hàng ID: " + payment.getOrderId(), e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin thanh toán.
     *
     * @param payment Đối tượng Payment với thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE dbo.payments SET order_id = ?, payment_method = ?, transaction_code = ?, amount = ?, payment_date = ?, status = ?, qr_code_data = ? WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setString(2, payment.getPaymentMethod());
            stmt.setString(3, payment.getTransactionCode());
            stmt.setBigDecimal(4, payment.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(6, payment.getStatus());
            stmt.setString(7, payment.getQrCodeData());
            stmt.setInt(8, payment.getPaymentId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật thanh toán ID: " + payment.getPaymentId(), e);
            return false;
        }
    }
    
    /**
     * Cập nhật trạng thái thanh toán.
     *
     * @param paymentId ID của thanh toán cần cập nhật.
     * @param newStatus Trạng thái mới.
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updatePaymentStatus(int paymentId, String newStatus) {
        String sql = "UPDATE dbo.payments SET status = ? WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, paymentId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật trạng thái thanh toán ID: " + paymentId, e);
            return false;
        }
    }

    /**
     * Lấy một bản ghi thanh toán theo ID.
     *
     * @param paymentId ID của thanh toán.
     * @return Đối tượng Payment nếu tìm thấy, ngược lại trả về null.
     */
    public Payment getPaymentById(int paymentId) {
        String sql = "SELECT * FROM dbo.payments WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thanh toán theo ID: " + paymentId, e);
        }
        return null;
    }
    
    /**
     * Lấy tất cả các bản ghi thanh toán liên quan đến một đơn hàng.
     *
     * @param orderId ID của đơn hàng.
     * @return Một danh sách các đối tượng Payment.
     */
    public List<Payment> getPaymentsByOrderId(int orderId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM dbo.payments WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapPaymentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thanh toán theo đơn hàng ID: " + orderId, e);
        }
        return payments;
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng Payment.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng payments.
     * @return Một đối tượng Payment đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Payment mapPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setTransactionCode(rs.getString("transaction_code"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        payment.setStatus(rs.getString("status"));
        payment.setQrCodeData(rs.getString("qr_code_data"));
        return payment;
    }
}
