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
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class BookManagementDAO {
    private static final Logger LOGGER = Logger.getLogger(BookManagementDAO.class.getName());
    
    private synchronized String generateNextMaSach() {
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
                pstmt.setString(4, book.getGenre());
                pstmt.setString(5, book.getDescription());
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
    
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE Sach SET TenSach = ?, TacGia = ?, TheLoai = ?, DonGia = ?, SoLuong = ?, MoTa = ? WHERE MaSach = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setBigDecimal(4, book.getPrice());
            ps.setInt(5, book.getQuantity());
            ps.setString(6, book.getDescription());
            ps.setString(7, book.getBookID());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật sách: " + book.getBookID(), ex);
            throw ex;
        }
    }
    
    public Book getBookById(String bookId) throws SQLException {
        Book book = null;
        String sql = "SELECT MaSach, TenSach, TacGia, TheLoai, DonGia, SoLuong, MoTa FROM Sach WHERE MaSach = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    return mapBookFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy sách theo ID: " + bookId, ex);
            throw ex;
        }
        return book;
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT MaSach, TenSach, TacGia, TheLoai, DonGia, SoLuong, MoTa FROM Sach";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapBookFromResultSet(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả sách", ex);
            throw ex;
        }
        return books;
    }
    
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Sach WHERE TenSach LIKE ? OR TacGia LIKE ? OR MaSach LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm sách với từ khóa: " + searchTerm, e);
        }
        return books;
    }
    
    private Book mapBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookID(rs.getString("MaSach"));
        book.setBookName(rs.getString("TenSach"));
        book.setAuthor(rs.getString("TacGia"));
        book.setGenre(rs.getString("TheLoai"));
        book.setPrice(rs.getBigDecimal("DonGia"));
        book.setQuantity(rs.getInt("SoLuong"));
        book.setDescription(rs.getString("MoTa"));
        return book;
    }
}
