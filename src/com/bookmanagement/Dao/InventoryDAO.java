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
    
    
    public boolean decreaseInventoryQuantity(int bookId, int warehouseId, int quantity) throws SQLException {
        Inventory existingInventory = getInventoryByBookAndWarehouse(bookId, warehouseId);
        if (existingInventory != null && existingInventory.getQuantity() >= quantity) {
            int newQuantity = existingInventory.getQuantity() - quantity;
            existingInventory.setQuantity(newQuantity);
            return updateInventory(existingInventory);
        } else {
            LOGGER.log(Level.WARNING, "Không đủ số lượng tồn kho hoặc không tìm thấy sách trong kho.");
            return false;
        }
    }
    
    public boolean increaseInventoryQuantity(int bookId, int warehouseId, int quantity) throws SQLException {
        Inventory existingInventory = getInventoryByBookAndWarehouse(bookId, warehouseId);
        if (existingInventory != null) {
            int newQuantity = existingInventory.getQuantity() + quantity;
            existingInventory.setQuantity(newQuantity);
            return updateInventory(existingInventory);
        } else {
            // Nếu không tìm thấy, có thể thêm mới một bản ghi tồn kho
            Inventory newInventory = new Inventory();
            newInventory.setBookId(bookId);
            newInventory.setWarehouseId(warehouseId);
            newInventory.setQuantity(quantity);
            return addInventory(newInventory);
        }
    }

    /**
     * Lấy tất cả các bản ghi tồn kho liên quan đến một cuốn sách.
     *
     * @param bookId ID của cuốn sách.
     * @return Một danh sách các đối tượng Inventory.
     */
    public int getStockQuantity(int bookId)  {
        String sql = "SELECT quantity FROM dbo.inventory WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException ex) {
            try {
                LOGGER.log(Level.SEVERE, "Lỗi khi lấy số lượng tồn kho của sách ID: " + bookId, ex);
                throw ex;
            } catch (SQLException ex1) {
                Logger.getLogger(InventoryDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return 0;
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
    
    
    public boolean updateOrCreateInventory(int bookId, int warehouseId, int quantity) {
        // Bước 1: Kiểm tra xem đã có tồn kho cho cuốn sách và kho này chưa.
        Inventory existingInventory = getInventoryByBookAndWarehouse(bookId, warehouseId);
        if (existingInventory != null) {
            // Bước 2: Nếu đã tồn tại, cập nhật số lượng.
            // Số lượng mới bằng số lượng hiện có cộng với số lượng nhập thêm.
            int newQuantity = existingInventory.getQuantity() + quantity;
            existingInventory.setQuantity(newQuantity);
            return updateInventory(existingInventory);
        } else {
            // Bước 3: Nếu chưa tồn tại, tạo một bản ghi mới.
            Inventory newInventory = new Inventory();
            newInventory.setBookId(bookId);
            newInventory.setWarehouseId(warehouseId);
            newInventory.setQuantity(quantity);
            return addInventory(newInventory);
        }
    }
    
    public boolean decreaseInventoryQuantity(int bookId, int quantity) throws SQLException {
        String checkSql = "SELECT quantity FROM inventory WHERE book_id = ?";
        String updateSql = "UPDATE inventory SET quantity = quantity - ? WHERE book_id = ? AND quantity >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
             PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

            // Kiểm tra số lượng tồn kho
            checkPstmt.setInt(1, bookId);
            ResultSet rs = checkPstmt.executeQuery();
            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                if (currentQuantity < quantity) {
                    LOGGER.log(Level.WARNING, "Không đủ tồn kho để giảm. Hiện có: {0}, cần giảm: {1}", new Object[]{currentQuantity, quantity});
                    return false;
                }
            } else {
                LOGGER.log(Level.WARNING, "Không tìm thấy sách với ID: {0}", bookId);
                return false;
            }

            // Tiến hành giảm số lượng
            updatePstmt.setInt(1, quantity);
            updatePstmt.setInt(2, bookId);
            updatePstmt.setInt(3, quantity); // Đảm bảo số lượng không âm
            int affectedRows = updatePstmt.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi giảm số lượng tồn kho", ex);
            throw ex;
        }
    }
    
    public boolean isStockAvailable(int bookId, int quantity) throws SQLException {
        String sql = "SELECT SUM(quantity) FROM dbo.inventory WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    int availableQuantity = rs.getInt(1);
                    return availableQuantity >= quantity;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra số lượng tồn kho cho sách ID: " + bookId, ex);
            throw ex;
        }
        return false;
    }
    
    /**
     * Cập nhật số lượng tồn kho cho một cuốn sách.
     * Phương thức này sẽ tìm bản ghi tồn kho đầu tiên cho cuốn sách
     * và cập nhật số lượng của nó.
     *
     * @param bookId ID của sách.
     * @param quantity Số lượng cần cập nhật (có thể là số âm để giảm).
     * @param isRefunded Nếu là true, sẽ hoàn lại vào kho (cộng dồn). Nếu false, sẽ trừ (giảm).
     * @return true nếu cập nhật thành công, false nếu không.
     * @throws SQLException nếu có lỗi xảy ra.
     */
    public boolean updateStockQuantity(int bookId, int quantity, boolean isRefunded) throws SQLException {
        String sql = "UPDATE dbo.inventory SET quantity = quantity + ? WHERE book_id = ?";
        if (!isRefunded) {
             sql = "UPDATE dbo.inventory SET quantity = quantity - ? WHERE book_id = ?";
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, bookId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật số lượng tồn kho cho sách ID: " + bookId, ex);
            throw ex;
        }
    }
    
    
    public void increaseInventoryQuantity(int bookId, int quantity) throws SQLException {
        String sql = "UPDATE inventory SET quantity = quantity + ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, bookId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.log(Level.INFO, "Đã cập nhật thành công tồn kho cho sách có ID: {0}", bookId);
            } else {
                LOGGER.log(Level.WARNING, "Không tìm thấy sách có ID: {0} để cập nhật tồn kho", bookId);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tăng số lượng tồn kho", ex);
            throw ex;
        }
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
