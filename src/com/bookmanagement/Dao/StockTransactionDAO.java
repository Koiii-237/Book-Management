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
import com.bookmanagement.model.StockTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StockTransactionDAO {
    private static final Logger LOGGER = Logger.getLogger(StockTransactionDAO.class.getName());

    /**
     * Thêm một giao dịch kho mới vào cơ sở dữ liệu.
     *
     * @param transaction đối tượng StockTransaction cần thêm.
     * @return true nếu thêm thành công, false nếu ngược lại.
     */
    public boolean addStockTransaction(StockTransaction transaction) {
        // SQL query để chèn dữ liệu. Sử dụng IDENTITY cho transaction_id nên không cần chèn.
        String sql = "INSERT INTO dbo.stock_transactions (book_id, from_warehouse_id, to_warehouse_id, quantity, transaction_type, transaction_date) VALUES (?, ?, ?, ?, ?, ?)";
        boolean success = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getBookId());
            // from_warehouse_id có thể là NULL, kiểm tra và set accordingly.
            if (transaction.getFromWarehouseId() != null) {
                pstmt.setInt(2, transaction.getFromWarehouseId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            // to_warehouse_id có thể là NULL, kiểm tra và set accordingly.
            if (transaction.getToWarehouseId() != null) {
                pstmt.setInt(3, transaction.getToWarehouseId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setInt(4, transaction.getQuantity());
            pstmt.setString(5, transaction.getTransactionType());
            // transaction_date có giá trị mặc định là GETDATE(), nhưng vẫn nên set nếu có.
            pstmt.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionDate()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm giao dịch kho: " + transaction, e);
        }
        return success;
    }

    /**
     * Lấy một giao dịch kho theo ID.
     *
     * @param transactionId ID của giao dịch.
     * @return đối tượng StockTransaction nếu tìm thấy, ngược lại là null.
     */
    public StockTransaction getStockTransactionById(int transactionId) {
        String sql = "SELECT * FROM dbo.stock_transactions WHERE transaction_id = ?";
        StockTransaction transaction = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    transaction = mapStockTransactionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy giao dịch kho với ID: " + transactionId, e);
        }
        return transaction;
    }

    /**
     * Lấy tất cả các giao dịch kho.
     *
     * @return một danh sách các đối tượng StockTransaction.
     */
    public List<StockTransaction> getAllStockTransactions() {
        List<StockTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM dbo.stock_transactions ORDER BY transaction_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapStockTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả giao dịch kho", e);
        }
        return transactions;
    }

    /**
     * Cập nhật một giao dịch kho.
     *
     * @param transaction đối tượng StockTransaction với thông tin đã cập nhật.
     * @return true nếu cập nhật thành công, false nếu ngược lại.
     */
    public boolean updateStockTransaction(StockTransaction transaction) {
        String sql = "UPDATE dbo.stock_transactions SET book_id = ?, from_warehouse_id = ?, to_warehouse_id = ?, quantity = ?, transaction_type = ?, transaction_date = ? WHERE transaction_id = ?";
        boolean success = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getBookId());
            if (transaction.getFromWarehouseId() != null) {
                pstmt.setInt(2, transaction.getFromWarehouseId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            if (transaction.getToWarehouseId() != null) {
                pstmt.setInt(3, transaction.getToWarehouseId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setInt(4, transaction.getQuantity());
            pstmt.setString(5, transaction.getTransactionType());
            pstmt.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionDate()));
            pstmt.setInt(7, transaction.getTransactionId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật giao dịch kho với ID: " + transaction.getTransactionId(), e);
        }
        return success;
    }

    /**
     * Xóa một giao dịch kho theo ID.
     *
     * @param transactionId ID của giao dịch cần xóa.
     * @return true nếu xóa thành công, false nếu ngược lại.
     */
    public boolean deleteStockTransaction(int transactionId) {
        String sql = "DELETE FROM dbo.stock_transactions WHERE transaction_id = ?";
        boolean success = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa giao dịch kho với ID: " + transactionId, e);
        }
        return success;
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng StockTransaction.
     *
     * @param rs ResultSet từ truy vấn cơ sở dữ liệu.
     * @return đối tượng StockTransaction đã được ánh xạ.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập dữ liệu từ ResultSet.
     */
    private StockTransaction mapStockTransactionFromResultSet(ResultSet rs) throws SQLException {
        StockTransaction transaction = new StockTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setBookId(rs.getInt("book_id"));
        
        // Cần kiểm tra NULL cho các cột from_warehouse_id và to_warehouse_id
        int fromWarehouseId = rs.getInt("from_warehouse_id");
        if (rs.wasNull()) {
            transaction.setFromWarehouseId(null);
        } else {
            transaction.setFromWarehouseId(fromWarehouseId);
        }
        
        int toWarehouseId = rs.getInt("to_warehouse_id");
        if (rs.wasNull()) {
            transaction.setToWarehouseId(null);
        } else {
            transaction.setToWarehouseId(toWarehouseId);
        }
        
        transaction.setQuantity(rs.getInt("quantity"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        
        Timestamp transactionDateTimestamp = rs.getTimestamp("transaction_date");
        if (transactionDateTimestamp != null) {
            transaction.setTransactionDate(transactionDateTimestamp.toLocalDateTime());
        }

        return transaction;
    }
}
