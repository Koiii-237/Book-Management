package com.bookmanagement.model;

import java.math.BigDecimal; // Import này cần thiết nếu giá là BigDecimal
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Lớp đại diện cho một cuốn sách trong hệ thống quản lý sách.
 */
public class Book {

    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String category;

    public Book() {
    }

    public Book(int bookId, String title, String author, String isbn, BigDecimal price, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
