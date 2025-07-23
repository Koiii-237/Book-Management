/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.Dao;

import com.bookmanagement.DBPool.DBConnection;
import com.bookmanagement.model.Book;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 *
 * @author ADMIN
 */
public class BookManagementDAO {
    private static final Logger LOGGER = Logger.getLogger(BookManagementDAO.class.getName());
    
    private String generateNextMaSach() {
        String maxID = null;
        String sql = "SELECT MAX(MaSach) FROM Sach WHERE MaSach LIKE 'BK%'"; 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                maxID = rs.getString(1);
            }

            int nextNumber = 1;
            if (maxID != null && maxID.matches("BK\\d{3,}")) { 
                try {
                    String numPart = maxID.substring(2); 
                    nextNumber = Integer.parseInt(numPart) + 1;
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Error when analyzing numbers from masach." + maxID + ". Bắt đầu lại từ BK001.", e);
                    nextNumber = 1;
                }
            }
            return String.format("BK%03d", nextNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi sinh mã sách tiếp theo: " + e.getMessage(), e);
            return "BK_ERROR_" + System.currentTimeMillis(); 
        }
    }
    
    public boolean addBook(Book book){
        String sql = "INSERT INTO Sach (MaSach, TenSach, TacGia, TheLoai, MoTa, DonGia) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);){
                
            String bookID = book.getBookID();
            if (bookID == null || bookID.isEmpty()) {
                bookID = generateNextMaSach(); 
                book.setBookID(bookID);
            }
                pstmt.setString(1, book.getBookID());
                pstmt.setString(2, book.getBookName());
                pstmt.setString(3, book.getAuthor());
                pstmt.setString(4, book.getCartegory());
                pstmt.setString(5, book.getDescibe());
                pstmt.setBigDecimal(6, book.getPrice());
                return pstmt.executeUpdate() > 0;
        } 
        catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { 
                LOGGER.log(Level.WARNING, "Lỗi: Mã sách '" + book.getBookID()+ "' đã tồn tại.", e);
                JOptionPane.showMessageDialog(null, "Mã sách '" + book.getBookID()+ "' đã tồn tại. Vui lòng nhập mã khác.", "Lỗi trùng mã", JOptionPane.ERROR_MESSAGE);
            } else {
                LOGGER.log(Level.SEVERE, "Lỗi khi thêm sách: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Lỗi khi thêm sách: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }
    
    public boolean deleteBook(String id){
        String sql = "DELETE FROM Sach WHERE MaSach = ?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)){
            
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBook(Book book){
        String sql = "UPDATE Sach SET TenSach = ?, TacGia = ?, TheLoai = ?, MoTa = ?, DonGia = ?, WHERE MaSach = ?";
        try (Connection con = DBConnection.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)){
            
            pstmt.setString(1, book.getBookName());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCartegory());
            pstmt.setString(4, book.getDescibe());
            pstmt.setBigDecimal(5, book.getPrice());
            pstmt.setString(6, book.getBookID());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Book readBookById(String id) {
        String sql = "SELECT * FROM Sach WHERE MaSach = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Book(
                    rs.getString("MaSach"),
                    rs.getString("TenSach"),
                    rs.getString("TacGia"),
                    rs.getString("TheLoai"),
                    rs.getString("MoTa"),
                    rs.getBigDecimal("DonGia")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy sách theo id." + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<Book> readAllBook(){
        ArrayList<Book> bookLS = new ArrayList<>();
        String sql = "SELECT * FROM Sach ORDER BY TenSach ASC";
        try (Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement()){
            
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                bookLS.add(new Book(
                        rs.getString("MaSach"),
                        rs.getString("TenSach"),
                        rs.getString("TacGia"),
                        rs.getString("TheLoai"),
                        rs.getString("MoTa"),
                        rs.getBigDecimal("DonGia"))
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả sách: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return bookLS;
    }
    
    public ArrayList<Book> searchBook(String search){
        ArrayList<Book> bookLS = new ArrayList<>();
        String sql = "SELECT * FROM Sach WHERE LOWER(MaSach) LIKE ? OR LOWER(TenSach) LIKE ? OR LOWER(TacGia) LIKE ? OR LOWER(TheLoai) LIKE ? ORDER BY TenSach ASC";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            String searchPattern = "%" + search.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            System.out.println("DEBUG: SQL Query for searchBooks: " + sql);
            System.out.println("DEBUG: Search Term (lowercase, with wildcards): " + searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                bookLS.add(new Book(
                        rs.getString("MaSach"),
                        rs.getString("TenSach"),
                        rs.getString("TacGia"),
                        rs.getString("TheLoai"),
                        rs.getString("MoTa"),
                        rs.getBigDecimal("DonGia"))
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm sách: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return bookLS;
    }
}
