/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookmanagement.model;

import java.math.BigDecimal;

/**
 *
 * @author ADMIN
 */
public class Book {
    private String bookID;
    private String author;
    private String bookName;
    private String cartegory;
    private String descibe;
    private BigDecimal price;

    public Book(String bookID, String author, String bookName, String cartegory, String descibe, BigDecimal price) {
        this.bookID = bookID;
        this.author = author;
        this.bookName = bookName;
        this.cartegory = cartegory;
        this.descibe = descibe;
        this.price = price;
    }

    public Book(String author, String bookName, String cartegory, String descibe, BigDecimal price) {
        this(null, author, bookName, cartegory, descibe, price);
    }
    
    public Book() {
    }

    public String getBookID() {
        
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getCartegory() {
        return cartegory;
    }

    public void setCartegory(String cartegory) {
        this.cartegory = cartegory;
    }

    public String getDescibe() {
        return descibe;
    }

    public void setDescibe(String descibe) {
        this.descibe = descibe;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    
}
