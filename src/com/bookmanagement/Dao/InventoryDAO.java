/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection; // Giả sử đã có lớp DBConnection
import com.bookmanagement.model.Inventory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
public class InventoryDAO {

    private static final Logger LOGGER = Logger.getLogger(InventoryDAO.class.getName());

    /**
     * Thêm một bản ghi tồn kho mới vào cơ sở dữ liệu.
     *
     * @param inventory Đối tượng Inventory cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi.
     */
    public boolean addInventory(Inventory inventory) {
        String sql = "INSERT INTO dbo.inventory (book_id, warehouse_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, inventory.getBookId());
            stmt.setInt(2, inventory.getWarehouseId());
            stmt.setInt(3, inventory.getQuantity());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        inventory.setInventoryId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm tồn kho cho sách ID " + inventory.getBookId() + " tại kho ID " + inventory.getWarehouseId(), e);
        }
        return false;
    }
    
    
    public List<Inventory> getAllInventories() throws SQLException {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT * FROM dbo.inventory";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                inventories.add(mapInventoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all inventories", e);
            throw e;
        }
        return inventories;
    }
    
    
    public Inventory getInventoryById(int inventoryId) throws SQLException {
        String sql = "SELECT * FROM dbo.inventory WHERE inventory_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapInventoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving inventory by ID: " + inventoryId, e);
            throw e;
        }
        return null;
    }
    
    /**
     * Cập nhật số lượng tồn kho của một cuốn sách tại một kho cụ thể.
     *
     * @param inventory Đối tượng Inventory với thông tin cập nhật (bookId,
     * warehouseId, quantity).
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm
     * thấy bản ghi.
     */
    public boolean updateInventory(Inventory inventory) {
        String sql = "UPDATE dbo.inventory SET book_id = ?, warehouse_id = ?, quantity = ? WHERE inventory_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inventory.getBookId());
            stmt.setInt(2, inventory.getWarehouseId());
            stmt.setInt(3, inventory.getQuantity());
            stmt.setInt(4, inventory.getInventoryId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật tồn kho.", e);
            return false;
        }
    }
    
    
    public boolean deleteInventory(int inventoryId) {
        String sql = "DELETE FROM dbo.inventory WHERE inventory_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inventoryId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa tồn kho.", e);
            return false;
        }
    }
    
    public boolean updateInventoryQuantity(int bookId, int warehouseId, int quantityChange) {
        try {
            // Lấy số lượng tồn kho hiện tại để kiểm tra
            int currentQuantity = getQuantityForBookInWarehouse(bookId, warehouseId);
            
            // Logic kiểm tra số lượng tồn kho âm
            if (currentQuantity + quantityChange < 0) {
                LOGGER.log(Level.WARNING, "Không đủ số lượng tồn kho để thực hiện giao dịch.");
                return false;
            }
            
            String sql = "UPDATE dbo.inventory SET quantity = quantity + ? WHERE book_id = ? AND warehouse_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quantityChange);
                stmt.setInt(2, bookId);
                stmt.setInt(3, warehouseId);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật số lượng tồn kho.", e);
            return false;
        }
    }
    
    
    public List<Inventory> searchInventories(String searchTerm) throws SQLException {
    List<Inventory> inventories = new ArrayList<>();
    String sql = "SELECT * FROM dbo.inventories WHERE inventory_id LIKE ? OR book_id LIKE ? OR warehouse_id LIKE ? OR quantity LIKE ?";
    try (Connection conn = DBConnection.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + searchTerm + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern);
        stmt.setString(4, searchPattern);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                inventories.add(mapInventoryFromResultSet(rs));
            }
        }
    }
    return inventories;
}
    
    /**
     * Lấy bản ghi tồn kho của một cuốn sách tại một kho cụ thể.
     *
     * @param bookId ID của cuốn sách.
     * @param warehouseId ID của kho hàng.
     * @return Đối tượng Inventory nếu tìm thấy, ngược lại trả về null.
     */
    public Inventory getInventoryByBookAndWarehouse(int bookId, int warehouseId) {
        String sql = "SELECT * FROM dbo.inventory WHERE book_id = ? AND warehouse_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, warehouseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapInventoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tồn kho của sách ID " + bookId + " tại kho ID " + warehouseId, e);
        }
        return null;
    }

    /**
     * Lấy tất cả các bản ghi tồn kho liên quan đến một cuốn sách.
     *
     * @param bookId ID của cuốn sách.
     * @return Một danh sách các đối tượng Inventory.
     */
    public List<Inventory> getInventoriesByBookId(int bookId) {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT * FROM dbo.inventory WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventories.add(mapInventoryFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tồn kho theo sách ID: " + bookId, e);
        }
        return inventories;
    }

    public int getQuantityForBookInWarehouse(int bookId, int warehouseId) throws SQLException {
        String sql = "SELECT quantity FROM dbo.inventories WHERE book_id = ? AND warehouse_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy bản ghi tồn kho
    }

    /**
     * Phương thức trợ giúp để ánh xạ một ResultSet thành một đối tượng
     * Inventory.
     *
     * @param rs ResultSet chứa dữ liệu của một hàng trong bảng inventory.
     * @return Một đối tượng Inventory đã được điền dữ liệu.
     * @throws SQLException nếu có lỗi xảy ra khi truy cập ResultSet.
     */
    private Inventory mapInventoryFromResultSet(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(rs.getInt("inventory_id"));
        inventory.setBookId(rs.getInt("book_id"));
        inventory.setWarehouseId(rs.getInt("warehouse_id"));
        inventory.setQuantity(rs.getInt("quantity"));
        return inventory;
    }
}
