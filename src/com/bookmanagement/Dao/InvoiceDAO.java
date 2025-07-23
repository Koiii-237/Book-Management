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
import com.bookmanagement.model.Invoice;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Statement;
public class InvoiceDAO {
    private static final Logger LOGGER = Logger.getLogger(InvoiceDAO.class.getName());

    // Create
    public void insert(Invoice inv) throws SQLException {
        inv.setInvoiceId(DBConnection.generateID("HoaDon","MaHD","HD"));
        String sql = "INSERT INTO HoaDon(MaHD, NgayTao, TongTien, PhuongThucThanhToan, TienKhachTra, TienThua, MaDH) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inv.getInvoiceId());
            // handle LocalDate conversion safely
            if (inv.getDateGenerate() != null) {
                ps.setDate(2, java.sql.Date.valueOf(inv.getDateGenerate()));
            } else {
                ps.setDate(2, null);
            }
            ps.setBigDecimal(3, inv.getMoneyTotal());
            ps.setString(4, inv.getPaymentMethod());
            ps.setBigDecimal(5, inv.getGuestMoney());
            ps.setBigDecimal(6, inv.getChange());
            ps.setString(7, inv.getOrderId());
            ps.executeUpdate();
        }
    }

    // Read by ID
    public Invoice findById(String id) throws SQLException {
        String sql="SELECT * FROM HoaDon WHERE MaHD=?";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,id);
            try (ResultSet rs=ps.executeQuery()) {
                if (rs.next()) {
                    return new Invoice(
                        rs.getString("MaHD"),
                        rs.getDate("NgayTao").toLocalDate(),
                        rs.getBigDecimal("TongTien"),
                        rs.getString("PhuongThucThanhToan"),
                        rs.getBigDecimal("TienKhachTra"),
                        rs.getBigDecimal("TienThua"),
                        rs.getString("MaDH")
                    );
                }
            }
        }
        return null;
    }

    // Read all
    public List<Invoice> getAll() throws SQLException {
        List<Invoice> list=new ArrayList<>();
        String sql="SELECT * FROM HoaDon";
        try (Connection conn=DBConnection.getConnection(); Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Invoice(
                    rs.getString("MaHD"),
                    rs.getDate("NgayTao").toLocalDate(),
                    rs.getBigDecimal("TongTien"),
                    rs.getString("PhuongThucThanhToan"),
                    rs.getBigDecimal("TienKhachTra"),
                    rs.getBigDecimal("TienThua"),
                    rs.getString("MaDH")
                ));
            }
        }
        return list;
    }

    // Update
    public void update(Invoice inv) throws SQLException {
        String sql = "UPDATE HoaDon SET NgayTao = ?, TongTien = ?, PhuongThucThanhToan = ?, TienKhachTra = ?, TienThua = ?, MaDH = ? WHERE MaHD = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (inv.getDateGenerate() != null) {
                ps.setDate(1, java.sql.Date.valueOf(inv.getDateGenerate()));
            } else {
                ps.setDate(1, null);
            }
            ps.setBigDecimal(2, inv.getMoneyTotal());
            ps.setString(3, inv.getPaymentMethod());
            ps.setBigDecimal(4, inv.getGuestMoney());
            ps.setBigDecimal(5, inv.getChange());
            ps.setString(6, inv.getOrderId());
            ps.setString(7, inv.getInvoiceId());
            ps.executeUpdate();
        }
    }

    // Delete
    public void delete(String id) throws SQLException {
        String sql="DELETE FROM HoaDon WHERE MaHD=?";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,id);
            ps.executeUpdate();
        }
    }

    // Read by Order
    public List<Invoice> findByOrder(String orderId) throws SQLException {
        List<Invoice> list=new ArrayList<>();
        String sql="SELECT * FROM HoaDon WHERE MaDH=?";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,orderId);
            try (ResultSet rs=ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Invoice(
                        rs.getString("MaHD"),
                        rs.getDate("NgayTao").toLocalDate(),
                        rs.getBigDecimal("TongTien"),
                        rs.getString("PhuongThucThanhToan"),
                        rs.getBigDecimal("TienKhachTra"),
                        rs.getBigDecimal("TienThua"),
                        rs.getString("MaDH")
                    ));
                }
            }
        }
        return list;
    }
}
