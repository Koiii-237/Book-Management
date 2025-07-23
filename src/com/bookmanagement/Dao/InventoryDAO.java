/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Book;
import com.bookmanagement.model.Inventory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class InventoryDAO {
    private static final Logger LOGGER = Logger.getLogger(InventoryDAO.class.getName());

    public boolean insert(Inventory inv) throws SQLException {
        inv.setInventoryId(DBConnection.generateID("Kho", "MaKho", "KHO"));
        String sql = "INSERT INTO Kho(MaKho, SoLuongTonKho, ViTri, MaSach) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inv.getInventoryId());
            ps.setInt(2, inv.getQunatity());
            ps.setString(3, inv.getAddress());
            ps.setString(4, inv.getBookId());
            return ps.executeUpdate() > 0;
            
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Inventory inv) throws SQLException {
        String sql = "UPDATE Kho SET SoLuongTonKho=?, ViTri=?, MaSach=? WHERE MaKho=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inv.getQunatity());
            ps.setString(2, inv.getAddress());
            ps.setString(3, inv.getBookId());
            ps.setString(4, inv.getInventoryId());
            return ps.executeUpdate() > 0;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String inventoryId) throws SQLException {
        String sql = "DELETE FROM Kho WHERE MaKho=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inventoryId);
            return ps.executeUpdate() > 0;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Inventory findById(String inventoryId) throws SQLException {
        String sql = "SELECT * FROM Kho WHERE MaKho=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inventoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Inventory(rs.getString("MaKho"), rs.getInt("SoLuongTonKho"),
                        rs.getString("ViTri"), rs.getString("MaSach"));
                }
            }
        }
        return null;
    }

    public ArrayList<Inventory> getAll() throws SQLException {
        ArrayList<Inventory> list = new ArrayList<>();
        String sql = "SELECT * FROM Kho";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Inventory(rs.getString("MaKho"), rs.getInt("SoLuongTonKho"),
                    rs.getString("ViTri"), rs.getString("MaSach")));
            }
        }
        return list;
    }

    public ArrayList<Inventory> searchInventory(String search) {
        ArrayList<Inventory> list = new ArrayList<>();
        String sql = "SELECT * FROM Kho WHERE LOWER(MaSach) LIKE ? OR LOWER(ViTri) LIKE ? ORDER BY SoLuongTonKho DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + search.toLowerCase() + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Inventory(rs.getString("MaKho"), rs.getInt("SoLuongTonKho"),
                        rs.getString("ViTri"), rs.getString("MaSach")));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm kho: " + e.getMessage(), e);
        }
        return list;
    }
}
