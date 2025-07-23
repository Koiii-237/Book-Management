/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Order;
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
import java.time.LocalDate;
import java.sql.Connection;

/**
 *
 * @author ADMIN
 */
public class OrderDAO {
    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());
    public boolean insertOrder(Order order) {
        String sql = "INSERT INTO DonHang (MaDonHang, NgayDatHang, TongTien, TrangThai, MaKH) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, order.getOrderId());
            ps.setDate(2, Date.valueOf(order.getOrderDate()));
            ps.setDouble(3, order.getTotalMoney());
            ps.setString(4, order.getRole());
            ps.setString(5, order.getCustomerId());
            return ps.executeUpdate() > 0;
        }
       catch (SQLException ex) {
           
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean updateOrder(Order order) {
        String sql = "UPDATE DonHang SET NgayDatHang=?, TongTien=?, TrangThai=?, MaKH=? WHERE MaDonHang=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setDate(1, Date.valueOf(order.getOrderDate()));
            ps.setDouble(2, order.getTotalMoney());
            ps.setString(3, order.getRole());
            ps.setString(4, order.getCustomerId());
            ps.setString(5, order.getOrderId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateStatus(String orderId, String newRole){
        String sql = "UPDATE DonHang SET TrangThai = ? WHERE MaDonHang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole);
            pstmt.setString(2, orderId);

            int affected = pstmt.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrder(String orderId) {
        String sql = "DELETE FROM DonHang WHERE MaDonHang=?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Order findById(String id){
        String sql="SELECT * FROM DonHang WHERE MaDonHang=?";
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,id);
            try(ResultSet rs=ps.executeQuery()){if(rs.next())return new Order(rs.getString("MaDonHang"),rs.getDate("NgayDatHang").toLocalDate(),rs.getDouble("TongTien"),rs.getString("TrangThai"),rs.getString("MaKH"));}
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ArrayList<Order> getAll(){
        ArrayList<Order> l=new ArrayList<>();String sql="SELECT * FROM DonHang";
        try(Connection conn=DBConnection.getConnection();Statement st=conn.createStatement();ResultSet rs=st.executeQuery(sql)){
            while(rs.next())l.add(new Order(rs.getString("MaDonHang"),rs.getDate("NgayDatHang").toLocalDate(),rs.getDouble("TongTien"),rs.getString("TrangThai"),rs.getString("MaKH")));
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return l;
    }
    
    public ArrayList<Order> searchOrder(String search){
        ArrayList<Order> list=new ArrayList<>();String sql="SELECT * FROM DonHang WHERE LOWER(MaDonHang) LIKE ? OR LOWER(TrangThai) LIKE ? OR LOWER(MaKH) LIKE ? ORDER BY NgayDatHang DESC";
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
            String p="%"+search.toLowerCase()+"%";ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);
            try(ResultSet rs=ps.executeQuery()){while(rs.next())list.add(new Order(rs.getString("MaDonHang"),rs.getDate("NgayDatHang").toLocalDate(),rs.getDouble("TongTien"),rs.getString("TrangThai"),rs.getString("MaKH")));}
        }catch(SQLException e){LOGGER.log(Level.SEVERE,e.getMessage(),e);}return list;
    }
}
