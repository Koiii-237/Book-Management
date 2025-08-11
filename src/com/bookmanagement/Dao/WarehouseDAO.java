/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Warehouse;
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
public class WarehouseDAO {
    private static final Logger LOGGER = Logger.getLogger(WarehouseDAO.class.getName());

    /**
     * Thêm một kho hàng mới vào cơ sở dữ liệu.
     * * @param warehouse Đối tượng Warehouse cần thêm.
     * @return Đối tượng Warehouse đã được thêm với ID mới.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy cập CSDL.
     */
    public Warehouse addWarehouse(Warehouse warehouse) throws SQLException {
        String query = "INSERT INTO dbo.warehouses (warehouse_name, location) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, warehouse.getWarehouseName());
            stmt.setString(2, warehouse.getLocation());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        warehouse.setWarehouseId(rs.getInt(1));
                    }
                }
            } else {
                throw new SQLException("Thêm kho hàng thất bại, không có hàng nào được thêm.");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm kho hàng", ex);
            throw ex;
        }
        return warehouse;
    }

    /**
     * Lấy tất cả các kho hàng từ cơ sở dữ liệu.
     * * @return Một danh sách các đối tượng Warehouse.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy cập CSDL.
     */
    public List<Warehouse> getAllWarehouses() throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM dbo.warehouses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                warehouses.add(mapWarehouseFromResultSet(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả kho hàng", ex);
            throw ex;
        }
        return warehouses;
    }
    
    /**
     * Lấy một kho hàng theo ID.
     * * @param warehouseId ID của kho hàng cần lấy.
     * @return Đối tượng Warehouse hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy cập CSDL.
     */
    public Warehouse getWarehouseById(int warehouseId) throws SQLException {
        Warehouse warehouse = null;
        String query = "SELECT * FROM dbo.warehouses WHERE warehouse_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    warehouse = mapWarehouseFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy kho hàng với ID: " + warehouseId, ex);
            throw ex;
        }
        return warehouse;
    }

    /**
     * Cập nhật thông tin của một kho hàng.
     * * @param warehouse Đối tượng Warehouse cần cập nhật.
     * @return true nếu cập nhật thành công, ngược lại là false.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy cập CSDL.
     */
    public boolean updateWarehouse(Warehouse warehouse) throws SQLException {
        String query = "UPDATE dbo.warehouses SET warehouse_name = ?, location = ? WHERE warehouse_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, warehouse.getWarehouseName());
            stmt.setString(2, warehouse.getLocation());
            stmt.setInt(3, warehouse.getWarehouseId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật kho hàng với ID: " + warehouse.getWarehouseId(), ex);
            throw ex;
        }
    }

    /**
     * Xóa một kho hàng khỏi cơ sở dữ liệu.
     * * @param warehouseId ID của kho hàng cần xóa.
     * @return true nếu xóa thành công, ngược lại là false.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy cập CSDL.
     */
    public boolean deleteWarehouse(int warehouseId) throws SQLException {
        String query = "DELETE FROM dbo.warehouses WHERE warehouse_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, warehouseId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa kho hàng với ID: " + warehouseId, ex);
            throw ex;
        }
    }
    
    /**
     * Phương thức hỗ trợ để ánh xạ dữ liệu từ ResultSet vào đối tượng Warehouse.
     * * @param rs ResultSet chứa dữ liệu của một hàng.
     * @return Một đối tượng Warehouse.
     * @throws SQLException Nếu có lỗi khi lấy dữ liệu từ ResultSet.
     */
    private Warehouse mapWarehouseFromResultSet(ResultSet rs) throws SQLException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(rs.getInt("warehouse_id"));
        warehouse.setWarehouseName(rs.getString("warehouse_name"));
        warehouse.setLocation(rs.getString("location"));
        return warehouse;
    }
}
