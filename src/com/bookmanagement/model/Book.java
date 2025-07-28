package com.bookmanagement.model;

import java.math.BigDecimal; // Import này cần thiết nếu giá là BigDecimal
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Lớp đại diện cho một cuốn sách trong hệ thống quản lý sách.
 */
public class Book {
    private String bookID;
    private String bookName;
    private String author;
    private String genre; // Thể loại
    private BigDecimal price; // Giá sách, sử dụng BigDecimal để chính xác hơn với tiền tệ
    private int quantity; // Số lượng sách trong kho - THUỘC TÍNH MỚI
    private String description; // Mô tả sách

    /**
     * Constructor mặc định.
     */
    public Book() {
    }

    public Book(String bookID, String bookName, String author, String genre,
                BigDecimal price, int quantity, String description) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.quantity = quantity; // Khởi tạo thuộc tính mới
        this.description = description;
    }

    public Book(String author, String name, String author0, String description, BigDecimal price) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // --- Getters và Setters ---

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Lấy số lượng sách tồn kho.
     * @return Số lượng sách.
     */
    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book{" +
               "bookID='" + bookID + '\'' +
               ", bookName='" + bookName + '\'' +
               ", author='" + author + '\'' +
               ", genre='" + genre + '\'' +
               ", price=" + price +
               ", quantity=" + quantity + // Thêm vào toString
               ", description='" + description + '\'' +
               '}';
    }
}
