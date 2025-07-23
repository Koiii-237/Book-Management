/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.OrderDetail;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

/**
 *
 * @author ADMIN
 */
public class OrderDetailDAO {
    private static final Logger LOGGER = Logger.getLogger(OrderDetailDAO.class.getName());

    // Create
    public void insert(OrderDetail d) throws SQLException {
        d.setId(DBConnection.generateID("ChiTietDonHang","MaCTDH","CTDH"));
        String sql="INSERT INTO ChiTietDonHang(MaCTDH,SoLuong,ThanhTien,DonGia,MaDH,MaSach) VALUES(?,?,?,?,?,?)";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,d.getId());
            ps.setInt(2,d.getQuantity());
            ps.setBigDecimal(3,d.getCoin());
            ps.setBigDecimal(4,d.getPrice());
            ps.setString(5,d.getOrderId());
            ps.setString(6,d.getBookId());
            ps.executeUpdate();
        }
    }

    // Read by ID
    public OrderDetail findById(String id) throws SQLException {
        String sql="SELECT * FROM ChiTietDonHang WHERE MaCTDH=?";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,id);
            try (ResultSet rs=ps.executeQuery()) {
                if (rs.next()) {
                    return new OrderDetail(
                        rs.getString("MaCTDH"),
                        rs.getInt("SoLuong"),
                        rs.getBigDecimal("ThanhTien"),
                        rs.getBigDecimal("DonGia"),
                        rs.getString("MaDH"),
                        rs.getString("MaSach")
                    );
                }
            }
        }
        return null;
    }

    // Read all
    public List<OrderDetail> getAll() throws SQLException {
        List<OrderDetail> list = new ArrayList<>();
        String sql="SELECT * FROM ChiTietDonHang";
        try (Connection conn=DBConnection.getConnection(); Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new OrderDetail(
                    rs.getString("MaCTDH"),
                    rs.getInt("SoLuong"),
                    rs.getBigDecimal("ThanhTien"),
                    rs.getBigDecimal("DonGia"),
                    rs.getString("MaDH"),
                    rs.getString("MaSach")
                ));
            }
        }
        return list;
    }

    // Read by Order
    public List<OrderDetail> findByOrder(String orderId) throws SQLException {
        List<OrderDetail> list = new ArrayList<>();
        String sql="SELECT * FROM ChiTietDonHang WHERE MaDH=?";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setString(1,orderId);
            try (ResultSet rs=ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderDetail(
                        rs.getString("MaCTDH"),
                        rs.getInt("SoLuong"),
                        rs.getBigDecimal("ThanhTien"),
                        rs.getBigDecimal("DonGia"),
                        rs.getString("MaDH"),
                        rs.getString("MaSach")
                    ));
                }
            }
        }
        return list;
    }

    // Update
    public void update(OrderDetail d) throws SQLException {
        String sql="UPDATE ChiTietDonHang SET SoLuong=?,ThanhTien=?,DonGia=?,MaDH=?,MaSach=? WHERE MaCTDH=?";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1, d.getQuantity());
            ps.setBigDecimal(2, d.getCoin());
            ps.setBigDecimal(3, d.getPrice());
            ps.setString(4, d.getOrderId());
            ps.setString(5, d.getBookId());
            ps.setString(6, d.getId());
            ps.executeUpdate();
        }
    }
}
