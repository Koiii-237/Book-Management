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
public class OrderDetail {
    private String id;
    private int quantity;
    private BigDecimal coin;
    private BigDecimal price;
    private String orderId;
    private String bookId;

    public OrderDetail(String id, int quantity, BigDecimal coin, BigDecimal price, String orderId, String bookId) {
        this.id = id;
        this.quantity = quantity;
        this.coin = coin;
        this.price = price;
        this.orderId = orderId;
        this.bookId = bookId;
    }

    public OrderDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
    
    
}
